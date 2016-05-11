package com.umeng.common.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.listener.PictureCheckListener;
import com.umeng.common.ui.model.PhotoModel;
import com.umeng.common.ui.polites.GestureImageView;


/**
 * 图片浏览视图
 * 
 */
public class PhotoPreview extends LinearLayout implements OnClickListener {

    private GestureImageView mImageView;
    private OnClickListener mClickListener;
    private PictureCheckListener mClickListenercheck;
//    private ImageView translateBg;
//    private Button checkButton;
//    private boolean ischeck;
    public PhotoPreview(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(
                ResFinder.getLayout("umeng_comm_imagepicker_view_photopreview"), this, true);

        mImageView = (GestureImageView) findViewById(ResFinder.getId("umeng_comm_iv_content_vpp"));
        mImageView.setOnClickListener(this);
//        translateBg = (ImageView)findViewById(ResFinder.getId("translate_bg"));
//        checkButton = (Button)findViewById(ResFinder.getId("check_image"));
//        checkButton.setOnClickListener(this);
    }

//    public void setIscheck(boolean ischeck) {
//        this.ischeck = ischeck;
//        checkButton.setSelected(ischeck);
//    }

//    public Button getCheckButton() {
//        return checkButton;
//    }
//
//    public ImageView getTranslateBg() {
//        return translateBg;
//    }

    public PhotoPreview(Context context, AttributeSet attrs, int defStyle) {
        this(context);
    }

    public PhotoPreview(Context context, AttributeSet attrs) {
        this(context);
    }

    public void loadImage(PhotoModel photoModel) {
        loadImage("file://" + photoModel.getOriginalPath());
    }

    private void loadImage(String path) {
        ImageLoaderManager.getInstance().getCurrentSDK().displayImage(path, mImageView);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }

    public void setOnClickListenerCheck(PictureCheckListener l) {
        this.mClickListenercheck = l;
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == ResFinder.getId("umeng_comm_iv_content_vpp")
                && mClickListener != null) {
            mClickListener.onClick(mImageView);
        }
//        else if (v.getId() == ResFinder.getId("check_image")
//                && mClickListenercheck != null){
////            mClickListenercheck.onClick(checkButton);
//        }

    }

}
