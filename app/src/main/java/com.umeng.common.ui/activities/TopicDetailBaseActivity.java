package com.umeng.common.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.mvpview.MvpTopicDetailView;
import com.umeng.common.ui.presenter.impl.TopicDetailPresenter;
import com.umeng.common.ui.widgets.TopicIndicator;


/**
 * Created by wangfei on 16/1/22.
 */
public abstract class TopicDetailBaseActivity extends BaseFragmentActivity implements View.OnClickListener,
        MvpTopicDetailView {
    protected TopicDetailPresenter mPresenter;
    protected Topic mTopic;
    protected String[] mTitles = null;
    protected TopicIndicator mIndicator;
    protected ViewPager mViewPager;
    protected FragmentPagerAdapter mAdapter;
    protected View postBtn;
    protected ToggleButton favouriteBtn;
    protected boolean isClick = true;
    protected TextView titleTextView;
    @Override
    public void setToggleButtonStatus(boolean status) {
        favouriteBtn.setClickable(true);
        favouriteBtn.setChecked(status);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_title_back_btn")) {
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mPresenter = new TopicDetailPresenter(this, this);
        setContentView(getLayout());
        mTopic = getIntent().getExtras().getParcelable(Constants.TAG_TOPIC);
        if (mTopic == null) {
            finish();
            return;
        }
        initTitles();
//        mTitles = getResources().getStringArray(
//                ResFinder.getResourceId(ResFinder.ResType.ARRAY, "umeng_commm_topic_detail_tabs"));
        // 根据话题的id信息初始化fragment
        initView();
        mPresenter.onCreate(arg0);
    }
    protected abstract void initTitles();
    protected void initView() {
        mIndicator = (TopicIndicator) findViewById(ResFinder.getId("indicator"));
        mViewPager = (ViewPager) findViewById(ResFinder.getId("viewPager"));
        mIndicator.setTabItemTitles(mTitles);
        mIndicator.setVisibleTabCount(mTitles.length);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int pos) {
                return getFragment(pos);
            }
        };
        mViewPager.setAdapter(mAdapter);
        // 设置关联的ViewPager
        mIndicator.setViewPager(mViewPager, 0);

        // 初始化Header的控件跟数据
//        initHeader();
        initTitle();
    }
    protected abstract int getLayout();
    protected abstract Fragment getFragment(int pos) ;
    protected void initTitle() {
        findViewById(ResFinder.getId("umeng_comm_title_back_btn")).setOnClickListener(this);
        titleTextView = (TextView) findViewById(ResFinder.getId("umeng_comm_title_tv"));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleTextView.setTextColor(ResFinder.getColor("umeng_comm_title"));
        titleTextView.setText(mTopic.name);
        findViewById(ResFinder.getId("umeng_comm_title_setting_btn")).setVisibility(View.GONE);
        favouriteBtn = (ToggleButton) findViewById(ResFinder.getId("umeng_comm_favourite_btn"));
        favouriteBtn.setVisibility(View.VISIBLE);
        postBtn = findViewById(ResFinder.getId("umeng_comm_post_btn"));
        postBtn.setVisibility(View.GONE);
        setTopicStatus();
        mPresenter.SetFavouriteButton(favouriteBtn);
        favouriteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                favouriteBtn.setClickable(false);


                CommonUtils.checkLoginAndFireCallback(TopicDetailBaseActivity.this,
                        new Listeners.SimpleFetchListener<LoginResponse>() {

                            @Override
                            public void onComplete(LoginResponse response) {
                                favouriteBtn.setChecked(!favouriteBtn.isChecked());
                                if (response.errCode != ErrorCode.NO_ERROR) {
                                    favouriteBtn.setChecked(!favouriteBtn.isChecked());
                                    return;
                                }
                                if (favouriteBtn.isChecked()) {
                                    mPresenter.cancelFollowTopic(mTopic);
                                } else {
                                    mPresenter.followTopic(mTopic);
                                }
                                isClick = true;

//                                    favouriteBtn.setClickable(true);
                            }
                        });

            }
        });
        postBtn.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void doAfterLogin(View v) {
                gotoPostFeedActivity();
            }
        });
        favouriteBtn.setTextOff("");
        favouriteBtn.setTextOn("");
        mIndicator.SetIndictorClick(new IndicatorListerner());
    }
    protected class IndicatorListerner implements TopicIndicator.IndicatorListener {
        @Override
        public void SetItemClick() {
            int cCount = mIndicator.getChildCount();
            for (int i = 0; i < cCount; i++) {
                final int j = i;
                View view = mIndicator.getChildAt(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            mViewPager.setCurrentItem(j);

                    }
                });
            }
        }
    }
    protected abstract void gotoPostFeedActivity();
    /**
     * 检查当前登录用户是否已关注该话题，并设置ToggleButton的状态</br>
     */
    protected void setTopicStatus() {
        String loginUserId = CommConfig.getConfig().loginedUser.id;
        if (TextUtils.isEmpty(loginUserId)) {
            Log.d("###", "### user dont login...");
            return;
        }
        com.umeng.comm.core.utils.Log.d("topic","is focused"+mTopic.isFocused);
        favouriteBtn.setChecked(mTopic.isFocused);
    }
}
