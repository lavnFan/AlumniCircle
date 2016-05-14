package com.seu.wufan.alumnicircle.ui.activity.me;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.MyInformationPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IMyInformationView;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class MyInformationActivity extends BaseSwipeActivity implements IMyInformationView {

    CommUser user = new CommUser();

    @Inject
    MyInformationPresenter myInformationPresenter;
    @Bind(R.id.my_information_photo_cv)
    CircleImageView mPhotoCv;
    @Bind(R.id.my_information_name_tv)
    TextView mNameTv;
    @Bind(R.id.my_information_college_tv)
    TextView mCollegeTv;
    @Bind(R.id.my_information_major_tv)
    TextView mMajorTv;
    @Bind(R.id.my_information_alummni_tv)
    TextView mAlummniTv;
    @Bind(R.id.my_information_dynamic_tv)
    TextView mDynamicTv;
    @Bind(R.id.my_information_label_tv)
    TextView mLabelTv;
    @Bind(R.id.my_information_job_tv)
    TextView mJobTv;
    @Bind(R.id.my_information_edu_tv)
    TextView mEduTv;
    @Bind(R.id.my_information_add_message_btn)
    Button addMessageBtn;
    @Bind(R.id.my_information_intro_tv)
    TextView mIntroTv;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_information;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        myInformationPresenter.attachView(this);
    }

    @Override
    protected void initViewsAndEvents() {
        user = getIntent().getExtras().getParcelable(Constants.TAG_USER);
        myInformationPresenter.getUserInfo(user.id);

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void initMyInfo(UserInfoDetailRes res) {
        mIntroTv.setText(res.getIntroduction());
    }

    @Override
    public void initMyInfo(UserInfoRes res) {
        if(!res.getImage().isEmpty()){
            CommonUtils.showCircleImageWithGlide(this,mPhotoCv,res.getImage());
        }

        mNameTv.setText(res.getName());
        String str = res.getEnroll_year()+"届"+res.getSchool();
        mCollegeTv.setText(str);
        mMajorTv.setText(res.getMajor());

    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(this);
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(this);
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myInformationPresenter.destroy();
    }

    @OnClick(R.id.my_information_add_message_btn)
    public void onClick() {


    }
}
