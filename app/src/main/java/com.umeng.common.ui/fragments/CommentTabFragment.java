/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.common.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.NullPresenter;


/**
 * 我的消息中评论模块下的两个tab Fragment
 */
public class CommentTabFragment extends BaseFragment<Void, NullPresenter> {

    CommentReceivedFragment mReceiveFragment;
    CommentPostedFragment mPostedFragment;

    //    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    //    private String[] mTitles = null;
    private FragmentPagerAdapter mAdapter;

    private TextView mCommentReceivedTv;
    private TextView mCommentPostedTv;
    private View mTabCursor;

    private int mScreenWidth;
    private int mCurrentTab;
    private int mTabDistance;

    private Class mUserInfoClass;
    private Class mTopicDetailClassName;
    private Class mFeedDetailClassName;

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_comment_main_layout");
    }

    @Override
    protected void initWidgets() {
        mScreenWidth = DeviceUtils.getScreenSize(getActivity()).x;

//        mTitles = getResources().getStringArray(
//                ResFinder.getResourceId(ResType.ARRAY, "umeng_comm_comments_tabs"));
//
//        mIndicator = (ViewPagerIndicator) findViewById(ResFinder
//                .getId("umeng_comm_comment_indicator"));

        mCommentReceivedTv = findViewById(ResFinder.getId("umeng_comm_comment_received"));
        mCommentReceivedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        mCommentPostedTv = findViewById(ResFinder.getId("umeng_comm_comment_posted"));
        mCommentPostedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
            }
        });

        mTabCursor = findViewById(ResFinder.getId("umeng_comm_tab_cursor"));

        mViewPager = (ViewPager) findViewById(ResFinder.getId("umeng_comm_comment_viewPager"));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                moveTabCurosr(i);
                changeSelectedTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
//        mIndicator.setTabItemTitles(mTitles);
        mAdapter = new FragmentPagerAdapter(getFragmentManager()) {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int pos) {
                return getFragment(pos);
            }
        };
        mViewPager.setAdapter(mAdapter);
        // 设置关联的ViewPager
//        mIndicator.setViewPager(mViewPager, 0);

        initTab();
    }

    /**
     * 获取对应的Fragment。0：话题聚合 1：活跃用户</br>
     *
     * @param pos
     * @return
     */
    private Fragment getFragment(int pos) {
        if (pos == 0) {
            if (mReceiveFragment == null) {
                mReceiveFragment = new CommentReceivedFragment();
                mReceiveFragment.setFeedDetailClassName(mFeedDetailClassName);
                mReceiveFragment.setTopicDetailClassName(mTopicDetailClassName);
                mReceiveFragment.setUserInfoClassName(mUserInfoClass);
            }
            return mReceiveFragment;
        } else if (pos == 1) {
            if (mPostedFragment == null) {
                mPostedFragment = new CommentPostedFragment();
                mPostedFragment.setFeedDetailClassName(mFeedDetailClassName);
                mPostedFragment.setTopicDetailClassName(mTopicDetailClassName);
                mPostedFragment.setUserInfoClassName(mUserInfoClass);
            }
            return mPostedFragment;
        }
        return null;
    }

    private void moveTabCurosr(int position) {
        float endPosition;
        float startPosition;

        if (mCurrentTab == position) {
            return;
        }

        if (mCurrentTab == 0) {
            startPosition = mTabDistance;
        } else {
            startPosition = CommonUtils.dip2px(getActivity(), 80) + mTabDistance * 3;
        }

        if (position == 0) {
            endPosition = mTabDistance;
        } else {
            endPosition = CommonUtils.dip2px(getActivity(), 80) + mTabDistance * 3;
        }

        mCurrentTab = position;
        TranslateAnimation animation = new TranslateAnimation(startPosition, endPosition, 0, 0);
        animation.setDuration(180);//设置动画持续时间
        animation.setFillAfter(true);
        mTabCursor.startAnimation(animation);
    }

    private void changeSelectedTab(int i) {
        mCommentReceivedTv.setSelected(i == 0);
        mCommentPostedTv.setSelected(i == 1);
    }

    private void initTab() {
        changeSelectedTab(0);

        mTabDistance = (mScreenWidth - CommonUtils.dip2px(getActivity(), 80 * 2)) / 4;
        TranslateAnimation animation = new TranslateAnimation(0, mTabDistance, 0, 0);
        animation.setDuration(0);//设置动画持续时间
        animation.setFillAfter(true);
        mTabCursor.startAnimation(animation);
    }

    public void setUserInfoClassName(Class userInfoClassName) {
        this.mUserInfoClass = userInfoClassName;
    }

    public void setTopicDetailClassName(Class topicDetailClassName) {
        this.mTopicDetailClassName = topicDetailClassName;
    }

    public void setFeedDetailClassName(Class feedDetailClassName) {
        this.mFeedDetailClassName = feedDetailClassName;
    }

}
