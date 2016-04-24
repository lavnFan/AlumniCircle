package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.App;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.component.ApiComponent;
import com.seu.wufan.alumnicircle.mvp.presenter.impl.WelcomePresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IWelcomeView;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/4/24
 *
 */
public class WelcomeActivity extends AppCompatActivity implements IWelcomeView{

    @Inject
    WelcomePresenter presenter;

    private Jump runnable;
    Thread thread;

    @Bind(R.id.skip_button)
    Button mSkipBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        getApiComponent().inject(this);
        presenter.attachView(this);
        initData();
    }

    private void initData() {
        runnable = new Jump(2000);
        thread = new Thread((runnable));
        thread.start();
    }

    @OnClick(R.id.skip_button)
    public void skipToLoginOrMain(){
        runnable.skip();
    }

    @Override
    public void showNetCantUse() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showToast(@NonNull String s) {

    }

    @Override
    public void readyToLogin() {
        readyGoThenKill(LoginActivity.class);
    }

    @Override
    public void readyToMain() {
        readyGoThenKill(MainActivity.class);
    }

    private class Jump implements Runnable{

        private long waitTime;
        private long startTime;
        private boolean running;
        private Handler handler;

        public Jump(long waitTime) {
            this.waitTime = waitTime;
            this.running = true;
            this.startTime = System.currentTimeMillis();
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            while(running){
                if(System.currentTimeMillis() - startTime >= waitTime){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            presenter.jumpActivity();
                        }
                    });
                    running = false;
                }
            }
        }

        public void skip(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    presenter.jumpActivity();
                }
            });
            running = false;
        }
    }

    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        finish();
    }

    public ApiComponent getApiComponent(){
        return ((App)getApplication()).getApiComponent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
        thread.interrupt();
        thread = null;
    }
}
