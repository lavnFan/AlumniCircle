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

package com.umeng.comm.ui.activities;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.comm.core.beans.BaseBean;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.listeners.Listeners.LoginOnViewClickListener;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.fragments.PostedFeedsFragment;
import com.umeng.comm.ui.fragments.PostedFeedsFragment.OnDeleteListener;
import com.umeng.common.ui.activities.AlbumActivity;
import com.umeng.common.ui.activities.UserInfoBaseActivity;
import com.umeng.common.ui.fragments.FansFragment;
import com.umeng.common.ui.fragments.FollowedUserFragment;
import com.umeng.common.ui.util.ViewFinder;


/**
 * 用户个人信息页面, 包含已发布的消息、已关注的话题、已关注的人三个fragment, 以及用户的头像、个人基本信息等.
 */
public final class UserInfoActivity extends UserInfoBaseActivity {

    /**
     * 已发送Feed的Fragment
     */
    private PostedFeedsFragment mPostedFragment = null;

    /**
     * 关注的好友Fragment
     */
    private FollowedUserFragment mFolloweredUserFragment;

    /**
     * 粉丝Fragment
     */
    private FansFragment mFansFragment;





    @Override
    public void initFragment() {
        mPostedFragment = PostedFeedsFragment.newInstance();
        mPostedFragment.setOnAnimationResultListener(mListener);
        // 视图查找器
        mViewFinder = new ViewFinder(getWindow().getDecorView());

        mPostedFragment.setCurrentUser(mUser);
        mPostedFragment.setOnDeleteListener(new OnDeleteListener() {

            @Override
            public void onDelete(BaseBean item) {
                mPresenter.decreaseFeedCount(1);
            }
        });
        addFragment(ResFinder.getId("umeng_comm_user_info_fragment_container"),
                mPostedFragment);
    }


    @Override
    public int GetLayout() {
        return ResFinder.getLayout("umeng_comm_user_info_layout");
    }

//    /**
//     * 隐藏评论布局</br>
//     */
//    private void hideCommentLayout() {
//        mCommentLayout.setVisibility(View.GONE);
//        hideInputMethod(mCommentEditText);
//
//    }
//
//    @SuppressWarnings("deprecation")
//    private void initUIComponents() {
//        // 设置Fragment
//
//
//        // 选中的某个tab时的文字颜色
//        mSelectedColor = ResFinder.getColor("umeng_comm_text_topic_light_color");
//
//        // 初始化feed、好友、粉丝、back、设置的listener
//        findViewById(ResFinder.getId("umeng_comm_posted_layout")).setOnClickListener(this);
//        findViewById(ResFinder.getId("umeng_comm_follow_user_layout")).setOnClickListener(
//                this);
//        findViewById(ResFinder.getId("umeng_comm_my_fans_layout")).setOnClickListener(this);
//        findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(this);
//
//        // 举报用户的Dialog
//        mReportDialog = new UserReportDialog(this);
//        mReportDialog.setTargetUid(mUser.id);
//
//        Button settingButton = (Button) findViewById(ResFinder.getId("umeng_comm_save_bt"));
//        settingButton.setBackgroundDrawable(ResFinder.getDrawable("umeng_comm_more"));
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) settingButton
//                .getLayoutParams();
//        params.width = DeviceUtils.dp2px(this, 20);
//        params.height = DeviceUtils.dp2px(this, 20);
//        params.rightMargin = DeviceUtils.dp2px(getApplicationContext(), 10);
//        settingButton.setLayoutParams(params);
//        settingButton.setOnClickListener(new LoginOnViewClickListener() {
//
//            @Override
//            protected void doAfterLogin(View v) {
//                mReportDialog.show();
//            }
//        });
//        // 如果是用户自己，则不显示设置菜单按钮【目前菜单只有举报功能，即自己不能举报自己】
//        if (mUser.id.equals(CommConfig.getConfig().loginedUser.id)
//                || mUser.permisson == Permisson.SUPPER_ADMIN) {
//            settingButton.setVisibility(View.GONE);
//        }
//
//        TextView titleTextView = mViewFinder.findViewById(ResFinder
//                .getId("umeng_comm_setting_title"));
//        titleTextView.setText(ResFinder.getString("umeng_comm_user_center"));
//        //
//        mPostedTv = mViewFinder.findViewById(ResFinder.getId("umeng_comm_posted_msg_tv"));
//        mPostedTv.setTextColor(mSelectedColor);
//
//        //
//        mPostedCountTv = mViewFinder.findViewById(ResFinder
//                .getId("umeng_comm_posted_count_tv"));
//        mPostedCountTv.setTextColor(mSelectedColor);
//
//        mFollowedUserTv = mViewFinder.findViewById(ResFinder.getId(
//                "umeng_comm_followed_user_tv"));
//        mFollowedUserCountTv = mViewFinder.findViewById(ResFinder.getId(
//                "umeng_comm_follow_user_count_tv"));
//
//        mFansTextView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_my_fans_tv"));
//        mFansCountTextView = mViewFinder.findViewById(ResFinder.getId(
//                "umeng_comm_fans_count_tv"));
//        // 昵称
//        mUserNameTv = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_name_tv"));
//        mUserNameTv.setText(mUser.name);
//
//        mHeaderImageView = mViewFinder.findViewById(ResFinder.getId(
//                "umeng_comm_user_header"));
//
//        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(mUser.gender);
//        mHeaderImageView.setImageUrl(mUser.iconUrl, option);
//
//        // 用户性别
//        mGenderImageView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_gender"));
//
//        // 关注按钮
//        mFollowToggleButton = mViewFinder.findViewById(ResFinder.getId(
//                "umeng_comm_user_follow"));
//        mFollowToggleButton.setOnClickListener(new Listeners.LoginOnViewClickListener() {
//
//            @Override
//            protected void doAfterLogin(View v) {
//                mFollowToggleButton.setClickable(false);
//                // true为选中状态为已关注，此时显示文本为“取消关注”；false代表未关注，此时显示文本为“关注”
//                if (mFollowToggleButton.isChecked()) {
//                    mPresenter.followUser(mResultListener);
//                } else {
//                    mPresenter.cancelFollowUser(mResultListener);
//                }
//            }
//        });
//
//        mAlbumTextView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_albums_tv"));
//        mAlbumTextView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                jumpToActivityWithUid(AlbumActivity.class);
//            }
//        });
//        mTopicTextView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_topic_tv"));
//        mTopicTextView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                jumpToActivityWithUid(FollowedTopicActivity.class);
//            }
//        });
//
//        // 用户自己(在未登录的情况下，点击设置跳转到登录，此时传递进来的uid是空的情况)，隐藏关注按钮，显示设置按钮
//        // // 如果是超级管理员且已经关注，则隐藏取消关注按钮
//        if (isHideFollowStatus()) {
//            mFollowToggleButton.setVisibility(View.GONE);
//        }
//        initCommentView();
//    }

    @Override
    protected void ListenerSet() {
        mFollowToggleButton.setOnClickListener(new LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                mFollowToggleButton.setClickable(false);
                // true为选中状态为已关注，此时显示文本为“取消关注”；false代表未关注，此时显示文本为“关注”
                if (!mUser.isFollowed) {
                    mPresenter.followUser(mResultListener);
                } else {
                    mPresenter.cancelFollowUser(mResultListener);
                }
            }
        });
        mAlbumTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jumpToActivityWithUid(AlbumActivity.class);
            }
        });
        mTopicTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jumpToActivityWithUid(FollowedTopicActivity.class);
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_posted_layout")) {// 已发消息
            if (mCurrentFragment instanceof PostedFeedsFragment) { // 已经处于当前页面，判断是否需要滚动到起始位置
                mPostedFragment.executeScrollToTop();
            } else {
                showFragment(mPostedFragment);
            }
            moveTabCurosr(0);
        } else if (id == ResFinder.getId("umeng_comm_follow_user_layout")) {// 关注用户
            if (mFolloweredUserFragment == null) {
                mFolloweredUserFragment = FollowedUserFragment.newInstance(mUser.id);
                mFolloweredUserFragment.setOnAnimationResultListener(mListener);
                mFolloweredUserFragment.setOnResultListener(mFollowListener);
            }
            if (mCurrentFragment instanceof FollowedUserFragment
                    && !(mCurrentFragment instanceof FansFragment)) {
                mFolloweredUserFragment.executeScrollTop();
            } else {
                showFragment(mFolloweredUserFragment);
            }
            moveTabCurosr(1);
        } else if (id == ResFinder.getId("umeng_comm_my_fans_layout")) { // 我的粉丝
            if (mFansFragment == null) {
                mFansFragment = FansFragment.newFansFragment(mUser.id);
                mFansFragment.setOnAnimationResultListener(mListener);
                mFansFragment.setOnResultListener(mFansListener);
            }
            if (mCurrentFragment instanceof FansFragment) {
                mFansFragment.executeScrollTop();
            } else {
                showFragment(mFansFragment);
            }
            moveTabCurosr(2);
        } else if (id == ResFinder.getId("umeng_comm_title_back_btn")) { // 返回
            this.finish();
        }
        changeSelectedText();
    }



    /**
     * 修改文本颜色 </br>
     */
    protected void changeSelectedText() {
        if ((mCurrentFragment instanceof PostedFeedsFragment)) {
            mFansCountTextView.setTextColor(Color.BLACK);
            changeTextColor(mSelectedColor, Color.BLACK, Color.BLACK);
        } else if ((mCurrentFragment instanceof FansFragment)) {
            changeTextColor(Color.BLACK, Color.BLACK, mSelectedColor);
        } else if ((mCurrentFragment instanceof FollowedUserFragment)) {
            changeTextColor(Color.BLACK, mSelectedColor, Color.BLACK);
        }
    }


    @Override
    protected void ReceiverComplete(CommUser user, boolean follow) {
        if (mFolloweredUserFragment != null) {
            mFolloweredUserFragment.updateFollowedState(user.id, follow);
        }
        if (mFansFragment != null) {
            mFansFragment.updateFollowedState(user.id, follow);
        }
    }

//    /**
//     * 数据同步处理
//     */
//    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
//
//        public void onReceiveFeed(Intent intent) {// 发送or删除时
//            FeedItem feedItem = getFeed(intent);
//            if (feedItem == null || !CommonUtils.isMyself(mUser)) {
//                return;
//            }
//
//            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
//            if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_POST == type) {
//                updateFeedTextView(++mUser.feedCount);
//            }
//        }
//
//        @Override
//        public void onReceiveUser(Intent intent) {
//            CommUser newUser = getUser(intent);
//            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
//            boolean follow = true;
//            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_FOLLOW) {
//                follow = true;
//            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_CANCEL_FOLLOW) {
//                follow = false;
//            }
//            if (mFansFragment != null) {
//                mFansFragment.updateFansList(newUser.id, follow);
//            }
//        }
//    };
}
