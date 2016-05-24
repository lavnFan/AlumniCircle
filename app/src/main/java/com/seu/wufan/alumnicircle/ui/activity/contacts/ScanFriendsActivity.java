package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.ScanFriendsPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IScanFriendsView;

import javax.inject.Inject;

import butterknife.Bind;
import cn.bingoogolapple.qrcode.core.QRCodeView;

/**
 * @author wufan
 * @date 2016/5/22
 */
public class ScanFriendsActivity extends BaseSwipeActivity implements QRCodeView.Delegate,IScanFriendsView{
    private static final String TAG = ScanFriendsActivity.class.getSimpleName();

    @Bind(R.id.zxingview)
    QRCodeView mQRCodeView;

    @Inject
    ScanFriendsPresenter scanFriendsPresenter;

    @Override
    protected void prepareDatas() {
        setToolbarTitle("扫描二维码加好友");
        getApiComponent().inject(this);
        scanFriendsPresenter.attachView(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scan_friends;
    }

    @Override
    protected void initViewsAndEvents() {
        mQRCodeView.setResultHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopSpot();
        mQRCodeView.stopCamera();
        super.onStop();

    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        String []res = result.split(" ");
        TLog.i("TAG",res[0]+" "+res[1]);
        vibrate();
        Bundle bundle = new Bundle();
        bundle.putString(SendAddFriendActivity.EXTRA_OTHER_ID, res[1]);
        bundle.putString(SendAddFriendActivity.EXTRA_MY_ITEM, res[0]);
        readyGo(SendAddFriendActivity.class, bundle);

        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }
    @Override
    protected View getLoadingTargetView() {
        return null;
    }
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.start_spot:
//                mQRCodeView.startSpot();
//                break;
//            case R.id.stop_spot:
//                mQRCodeView.stopSpot();
//                break;
//            case R.id.start_spot_showrect:
//                mQRCodeView.startSpotAndShowRect();
//                break;
//            case R.id.stop_spot_hiddenrect:
//                mQRCodeView.stopSpotAndHiddenRect();
//                break;
//            case R.id.show_rect:
//                mQRCodeView.showScanRect();
//                break;
//            case R.id.hidden_rect:
//                mQRCodeView.hiddenScanRect();
//                break;
//            case R.id.start_preview:
//                mQRCodeView.startCamera();
//                break;
//            case R.id.stop_preview:
//                mQRCodeView.stopCamera();
//                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
        }
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
}
