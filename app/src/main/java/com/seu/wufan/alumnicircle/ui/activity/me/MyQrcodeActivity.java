package com.seu.wufan.alumnicircle.ui.activity.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.User;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtil;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.QRGenerateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyQrcodeActivity extends BaseSwipeActivity {

    @Bind(R.id.my_qrcode_image_view)
    ImageView qrCodeImageView;
    @Bind(R.id.my_qrcode_photo_cv)
    CircleImageView mPhotoCv;
    @Bind(R.id.my_qrcode_name_tv)
    TextView mNameTv;
    @Bind(R.id.my_qrcode_school_tv)
    TextView mSchoolTv;

    private Bitmap mBitmap;
    private Bitmap mQrcode;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_qrcode;
    }

    @Override
    protected void prepareDatas() {

        setToolbarTitle(getResources().getString(R.string.my_qrcode));
    }

    @Override
    protected void initViewsAndEvents() {
        final User user = (User) PreferenceUtil.getBean(this,PreferenceUtil.Key.EXTRA_COMMUSER);
        TLog.i("TAG",user.getUmeng_id()+" "+user.getName()+" "+user.getImage());
        mNameTv.setText(user.getName());
        String sSchoolMajor = user.getSchool()+" "+user.getMajor();
        mSchoolTv.setText(sSchoolMajor);
        CommonUtils.showCircleImageWithGlide(this,mPhotoCv,user.getImage());

        final String userInfo = user.getName()+" "+user.getUser_id();
        Glide.with(this).load(user.getImage()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mBitmap = resource;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mQrcode = QRGenerateUtil.createQRImage(userInfo, 600, 600, mBitmap);
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
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

}