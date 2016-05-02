package com.seu.wufan.alumnicircle.ui.widget.qrcode.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;

public class InactivityTimer {

	private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

	private Activity activity;
	private BroadcastReceiver powerStatusReceiver;
	private boolean registered;
	private AsyncTask<Object, Object, Object> inactivityTask;

	public InactivityTimer(Activity activity) {
		this.activity = activity;
		powerStatusReceiver = new PowerStatusReceiver();
		registered = false;
		onActivity();
	}

	@SuppressLint("NewApi")
	public synchronized void onActivity() {
		cancel();
		inactivityTask = new InactivityAsyncTask();
		if (Build.VERSION.SDK_INT >= 11) {
			inactivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			inactivityTask.execute();
		}
	}

	public synchronized void onPause() {
		cancel();
		if (registered) {
			activity.unregisterReceiver(powerStatusReceiver);
			registered = false;
		}
	}

	public synchronized void onResume() {
		if (!registered) {
			activity.registerReceiver(powerStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			registered = true;
		}
		onActivity();
	}

	private synchronized void cancel() {
		AsyncTask<?, ?, ?> task = inactivityTask;
		if (task != null) {
			task.cancel(true);
			inactivityTask = null;
		}
	}

	public void shutdown() {
		cancel();
	}

	private class PowerStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				// 0 indicates that we're on battery
				boolean onBatteryNow = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) <= 0;
				if (onBatteryNow) {
					InactivityTimer.this.onActivity();
				} else {
					InactivityTimer.this.cancel();
				}
			}
		}
	}

	private class InactivityAsyncTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... objects) {
			try {
				Thread.sleep(INACTIVITY_DELAY_MS);
				activity.finish();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
