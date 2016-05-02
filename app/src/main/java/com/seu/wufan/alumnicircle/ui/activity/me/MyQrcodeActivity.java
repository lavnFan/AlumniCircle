package com.seu.wufan.alumnicircle.ui.activity.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.QRGenerateUtil;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyQrcodeActivity  extends BaseSwipeActivity {

    @Bind(R.id.my_qrcode_image_view)
    ImageView qrCodeImageView;

    private Bitmap mBitmap;
    private Bitmap mQrcode;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_qrcode;
    }

    @Override
    protected void prepareDatas() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        setToolbarTitle(getResources().getString(R.string.my_qrcode));
    }

    @Override
    protected void initViewsAndEvents() {
        final String s = "东南大学";
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQrcode = QRGenerateUtil.createQRImage(s, 600, 600, mBitmap);
                if (mQrcode != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeImageView.setImageBitmap(mQrcode);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}