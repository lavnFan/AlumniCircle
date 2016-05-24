package com.seu.wufan.alumnicircle.ui.widget.qrcode.dwj;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.camera.CameraManager;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.decode.CaptureHandler;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.decode.DecodeUtils;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.DensityUtil;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.InactivityTimer;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.TextUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @Bind(R.id.capture_preview)
    SurfaceView mSurfaceView;
    @Bind(R.id.capture_error_mask)
    ImageView mErrorMask;
    @Bind(R.id.capture_scan_mask)
    ImageView mScanMask;
    @Bind(R.id.capture_crop_view)
    FrameLayout mScanArea;
    @Bind(R.id.capture_light_btn)
    Button mLightBtn;
    @Bind(R.id.capture_container)
    RelativeLayout mContainer;
    @Bind(R.id.switch_mode_btn)
    Button mSwitchModeBtn;

    private boolean hasSurface;
    private boolean isLightOn;
    private boolean scanBarCode;

    private int mQrcodeCropWidth;
    private int mQrcodeCropHeight;
    private int mBarcodeCropWidth;
    private int mBarcodeCropHeight;

    private float mScanAreaWitdh;
    private float mScanAreaHeight;

    private CameraManager cameraManager;
    private CaptureHandler handler;
    private InactivityTimer mInactivityTimer;
    private Rect cropRect;
    private int dataMode = DecodeUtils.DECODE_DATA_MODE_QRCODE;
    private ObjectAnimator mScanMaskObjectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);
        ButterKnife.bind(this);
        new Handler();
        mInactivityTimer = new InactivityTimer(ScanActivity.this);

        mQrcodeCropWidth = DensityUtil.dip2px(this, 210);
        mQrcodeCropHeight = DensityUtil.dip2px(this, 210);
        mBarcodeCropWidth = DensityUtil.dip2px(this, 280);
        mBarcodeCropHeight = DensityUtil.dip2px(this, 140);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化相机
        cameraManager = new CameraManager(getApplicationContext());

        if (hasSurface) {
            initCamera(mSurfaceView.getHolder());
        } else {
            mSurfaceView.getHolder().addCallback(this);
        }
        mInactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        mInactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!hasSurface) mSurfaceView.getHolder().removeCallback(this);
        if (null != mScanMaskObjectAnimator && mScanMaskObjectAnimator.isStarted())
            mScanMaskObjectAnimator.cancel();
        super.onPause();
    }

    @OnClick(R.id.capture_light_btn)
    public void lightSwitch(View v) {
        if (isLightOn) {
            mLightBtn.setText("开灯");
            cameraManager.setTorch(false);
            mLightBtn.setSelected(false);
        } else {
            mLightBtn.setText("关灯");
            cameraManager.setTorch(true);
            mLightBtn.setSelected(true);
        }
        isLightOn = !isLightOn;
    }

    @OnClick(R.id.switch_mode_btn)
    public void modeSwitch(View v) {
        if (scanBarCode) {
            mSwitchModeBtn.setText("条形码");
            doSwitchModeAnim(true);
        } else {
            mSwitchModeBtn.setText("二维码");
            doSwitchModeAnim(false);
        }
        scanBarCode = !scanBarCode;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera(holder);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null && !cameraManager.isOpen()) {
            try {
                cameraManager.openDriver(surfaceHolder);
                if (handler == null) handler = new CaptureHandler(this, cameraManager);
                onCameraPreviewSuccess();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "打开相机失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onCameraPreviewSuccess() {
        initCrop();
        mErrorMask.setVisibility(View.GONE);
        startScanMaskAnim();
    }

    public void handleDecode(String result, Bundle bundle) {
        mInactivityTimer.onActivity();

        if (!TextUtil.isEmpty(result) && TextUtil.isUrl(result)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(result));
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ResultActivity.class);
            bundle.putString("result", result);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void startScanMaskAnim() {
        ViewHelper.setPivotY(mScanMask, 0);
        mScanMaskObjectAnimator = ObjectAnimator.ofFloat(mScanMask, "scaleY", 0, 1);
        mScanMaskObjectAnimator.setDuration(2000);
        mScanMaskObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mScanMaskObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mScanMaskObjectAnimator.start();
    }

    private void doSwitchModeAnim(final boolean bar2qr) {
        if (bar2qr) {
            mScanAreaWitdh = (float) mQrcodeCropWidth / mBarcodeCropWidth;
            mScanAreaHeight = (float) mQrcodeCropHeight / mBarcodeCropHeight;
        } else {
            mScanAreaWitdh = (float) mBarcodeCropWidth / mQrcodeCropWidth;
            mScanAreaHeight = (float) mBarcodeCropHeight / mQrcodeCropHeight;
        }

        PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", 1, mScanAreaWitdh);
        PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", 1, mScanAreaHeight);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(widthHolder, heightHolder);
        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float dynamicWidth = (Float) animation.getAnimatedValue("width");
                Float dynamicHeight = (Float) animation.getAnimatedValue("height");

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mScanArea.getLayoutParams();
                if (bar2qr) {
                    params.width = (int) (mBarcodeCropWidth * dynamicWidth);
                    params.height = (int) (mBarcodeCropHeight * dynamicHeight);
                } else {
                    params.width = (int) (mQrcodeCropWidth * dynamicWidth);
                    params.height = (int) (mQrcodeCropHeight * dynamicHeight);
                }
                mScanArea.setLayoutParams(params);

                if (dynamicWidth == mScanAreaWitdh && dynamicHeight == mScanAreaHeight) {
                    initCrop();
                    setDataMode(bar2qr ? DecodeUtils.DECODE_DATA_MODE_QRCODE : DecodeUtils.DECODE_DATA_MODE_BARCODE);
                }
            }
        });
    }

    public void initCrop() {
        //相机的实际宽高
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;
        //相机显示在屏幕中的宽高
        int containerWidth = mContainer.getWidth();
        int containerHeight = mContainer.getHeight();
        //扫描区域的宽高
        int cropWidth = mScanArea.getWidth();
        int cropHeight = mScanArea.getHeight();
        //扫描区域在相机中的实际位置
        int[] location = new int[2];
        mScanArea.getLocationInWindow(location);
        int x = location[0] * cameraWidth / containerWidth;
        int y = location[1] * cameraHeight / containerHeight;
        //扫描区域在相机中的实际宽高
        int width = cropWidth * cameraWidth / containerWidth;
        int height = cropHeight * cameraHeight / containerHeight;
        //设置扫描的矩形区域
        setCropRect(new Rect(x, y, width + x, height + y));
    }

    public Rect getCropRect() {
        return cropRect;
    }

    public void setCropRect(Rect cropRect) {
        this.cropRect = cropRect;
    }

    public int getDataMode() {
        return dataMode;
    }

    public void setDataMode(int dataMode) {
        this.dataMode = dataMode;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
}
