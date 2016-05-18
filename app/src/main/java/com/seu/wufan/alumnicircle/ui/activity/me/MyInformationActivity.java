package com.seu.wufan.alumnicircle.ui.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.MyInformationPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IMyInformationView;
import com.seu.wufan.alumnicircle.ui.adapter.me.EduMyItemAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.me.JobItemAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.me.JobMyItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.TimeUtils;
import com.umeng.comm.ui.activities.UserInfoActivity;

import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class MyInformationActivity extends BaseSwipeActivity implements IMyInformationView {

    CommUser user = new CommUser();

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;

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
    @Bind(R.id.my_information_job_history_lv)
    ScrollLoadMoreListView mJobHistoryLv;
    @Bind(R.id.my_information_edu_history_lv)
    ScrollLoadMoreListView mEduHistoryLv;
    @Bind(R.id.my_information_add_message_btn)
    Button mAddMessageBtn;
    @Bind(R.id.my_information_per_intro_tv)
    TextView mIntroTv;

    @Bind(R.id.my_info_dynamic_ll)
    LinearLayout mDynamicLl;
    @Bind(R.id.my_info_dynamic_iv)
    ImageView mDynamicIv;
    @Bind(R.id.my_info_dynamic_text_tv)
    TextView mDynamicTv;
    @Bind(R.id.my_info_dynamic_time_tv)
    TextView mDynamicTimeTv;
    @Bind(R.id.my_info_dynamic_ll_none)
    LinearLayout mDynamicLlNone;

    @Bind(R.id.my_information_scroll_view)
    ScrollView mScrollView;

    JobMyItemAdapter jobMyItemAdapter;
    EduMyItemAdapter eduMyItemAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_information;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        myInformationPresenter.attachView(this);
    }

    private void initToolbar(String name) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(name);
    }

    @Override
    protected void initViewsAndEvents() {
        user = getIntent().getExtras().getParcelable(Constants.TAG_USER);
        myInformationPresenter.initUser(user.sourceUid);
        mScrollView.smoothScrollTo(0, 0);
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void initMyInfo(UserInfoDetailRes res) {
        mIntroTv.setText(res.getIntroduction());

        jobMyItemAdapter = new JobMyItemAdapter(this);
        jobMyItemAdapter.setmEntities(res.getJobHistory());
        mJobHistoryLv.setAdapter(jobMyItemAdapter);
        jobMyItemAdapter.notifyDataSetChanged();

        eduMyItemAdapter = new EduMyItemAdapter(this);
        eduMyItemAdapter.setmEntities(res.getEduHistory());
        mEduHistoryLv.setAdapter(eduMyItemAdapter);
        eduMyItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void initMyInfo(UserInfoRes res) {
        if (!res.getImage().isEmpty()) {
            CommonUtils.showCircleImageWithGlide(this, mPhotoCv, res.getImage());
        }
        initToolbar(res.getName());
        mNameTv.setText(res.getName());
        String str = res.getEnroll_year() + "届" + res.getSchool();
        mCollegeTv.setText(str);
        mMajorTv.setText(res.getMajor());
    }

    @Override
    public void initDynamic(FeedItem feedItem, boolean noEmpty) {
        if (noEmpty) {  //如果不为空
            mDynamicLlNone.setVisibility(View.GONE);
            mDynamicLl.setVisibility(View.VISIBLE);
            mDynamicTv.setText(feedItem.text);
            Date date = new Date(Long.parseLong(feedItem.publishTime));
            mDynamicTimeTv.setText(TimeUtils.format(date));
            if (!feedItem.getImages().isEmpty()) {
                CommonUtils.showImageWithGlide(this, mDynamicIv, feedItem.getImages().get(0).originImageUrl);
            }
        } else {
            mDynamicLl.setVisibility(View.GONE);
            mDynamicLlNone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void goDynamic(CommUser user) {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(MyDynamicActivity.EXTRA_COMM_USER,user);
//        readyGo(MyDynamicActivity.class,bundle);

        Intent intent = new Intent(this,
                UserInfoActivity.class);
        intent.putExtra(Constants.TAG_USER, user);
        startActivity(intent);
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

    @OnClick(R.id.my_info_dynamic_go_ll)
    void goDynamicDetail() {
        myInformationPresenter.getCommUser(user.sourceUid);
        TLog.i("TAG", user.sourceUid);
    }

}
