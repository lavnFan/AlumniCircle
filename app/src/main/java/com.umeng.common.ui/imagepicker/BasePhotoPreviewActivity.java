
package com.umeng.common.ui.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.model.PhotoModel;
import com.umeng.common.ui.util.AnimationUtil;
import com.umeng.common.ui.widgets.PhotoPreview;

import java.util.ArrayList;
import java.util.List;

public class BasePhotoPreviewActivity extends Activity implements OnPageChangeListener,
        OnClickListener {

    private ViewPager mViewPager;
    private RelativeLayout layoutTop;
    private ImageView btnBack;
    private TextView tvPercent,checkPrecent;
    protected List<PhotoModel> photos;
    protected ArrayList<PhotoModel> selectedphotos =new ArrayList<>();
    protected List<String> photosUris = new ArrayList<>();
    protected int current;
    protected int MAXNUM;
    protected ImageView translateBg;
    protected Button checkButton;
    protected RelativeLayout img_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(ResFinder.getLayout("umeng_comm_imagepicker_photopreview"));
        layoutTop = (RelativeLayout) findViewById(ResFinder.getId("umeng_comm_layout_top_app"));
        img_layout =  (RelativeLayout) findViewById(ResFinder.getId("transhlate_layout"));
        btnBack = (ImageView) findViewById(ResFinder.getId("umeng_comm_btn_back_app"));
        tvPercent = (TextView) findViewById(ResFinder.getId("umeng_comm_tv_percent_app"));
        checkPrecent = (TextView) findViewById(ResFinder.getId("umeng_comm_check_percent_app"));
        checkPrecent.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(ResFinder.getId("umeng_comm_vp_base_app"));
        translateBg = (ImageView)findViewById(ResFinder.getId("translate_bg"));
        checkButton = (Button)findViewById(ResFinder.getId("check_image"));
        checkButton.setOnClickListener(this);
        checkPrecent.setText("下一步（2/9）");
        btnBack.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);

        overridePendingTransition(
                ResFinder.getResourceId(ResType.ANIM, "umeng_comm_activity_alpha_action_in"), 0); // 渐入效果

    }

    /** 绑定数据，更新界面 */
    protected void bindData() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
        if(current == 0){
            // setCurrentItem(0) 不会执行onPageSelected()方法
            changeCheckBtnWithPosition(current);
        }
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (photos == null) {
                return 0;
            } else {
                return photos.size();
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoPreview photoPreview = new PhotoPreview(getApplicationContext());
            photoPreview.setId(position);
            ((ViewPager) container).addView(photoPreview);
            photoPreview.loadImage(photos.get(position));
            photoPreview.setOnClickListener(photoItemClickListener);
//            photoPreview.setOnClickListenerCheck(new PictureCheckListener() {
//                @Override
//                public void onClick(View view) {
//                    if (view.isSelected()){
//                        view.setSelected(false);
//                        selectedphotos.remove(photos.get(position));
//                    }else {
//                        if (selectedphotos.size() == MAXNUM){
//                            ToastMsg.showShortMsgByResName("不能选择更多图片");
//                            return;
//                        }
//                        view.setSelected(true);
//                        selectedphotos.add(photos.get(position));
//                    }
//                   updatePercent();
//                }
//            });
            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };
    protected boolean isUp;

    @Override
    public void onClick(View v) {
        if (v.getId() == ResFinder.getId("umeng_comm_btn_back_app")) {
            setResultOnFinish(0);
        }
        if (v.getId() == ResFinder.getId("umeng_comm_check_percent_app")){
            setResultOnFinish(1);
        }
        if (v.getId() == ResFinder.getId("check_image")){
            if (v.isSelected()){
                    v.setSelected(false);
                    selectedphotos.remove(photos.get(mViewPager.getCurrentItem()));

            }else {
                if (selectedphotos.size() == MAXNUM){
                    ToastMsg.showShortMsgByResName("umeng_comm_cannot_select_more_photos");
                    return;
                }
                v.setSelected(true);
                selectedphotos.add(photos.get(mViewPager.getCurrentItem()));
            }
            updatePercent();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        changeCheckBtnWithPosition(position);
    }

    protected void changeCheckBtnWithPosition(int position){
        current = position;
        updatePercent();
        if(!selectedphotos.isEmpty()){
            if (selectedphotos.contains(photos.get(position))){
                checkButton.setSelected(true);
            }else{
                checkButton.setSelected(false);
            }
        }else{
            checkButton.setSelected(photos.get(position).isChecked());
        }
    }

    protected void updatePercent() {
        tvPercent.setText((current + 1) + "/" + photos.size());
        checkPrecent.setText("下一步"+"("+selectedphotos.size()+"/"+MAXNUM+")");
    }

    /** 图片点击事件回调 */
    private OnClickListener photoItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isUp) {
                new AnimationUtil(getApplicationContext(), ResFinder.getResourceId(ResType.ANIM,
                        "umeng_comm_translate_up"))
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true)
                        .startAnimation(layoutTop);
                new AnimationUtil(getApplicationContext(), ResFinder.getResourceId(ResType.ANIM,
                        "umeng_comm_translate_up_img"))
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true)
                        .startAnimation(img_layout);


                isUp = true;
            } else {
                new AnimationUtil(getApplicationContext(), ResFinder.getResourceId(ResType.ANIM,
                        "umeng_comm_translate_down_current"))
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true)
                        .startAnimation(layoutTop);
                new AnimationUtil(getApplicationContext(), ResFinder.getResourceId(ResType.ANIM,
                        "umeng_comm_translate_down_img"))
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true)
                        .startAnimation(img_layout);

                isUp = false;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResultOnFinish(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setResultOnFinish(int result){
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected", selectedphotos);
        Intent intent = new Intent();
        intent.putExtra("bundle",bundle);
        setResult(result, intent);
        finish();
    }
}
