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

package com.umeng.common.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.CommUser.Gender;
import com.umeng.comm.core.beans.CommUser.Permisson;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.listeners.Listeners.LoginOnViewClickListener;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.anim.CustomAnimator;
import com.umeng.common.ui.anim.UserInfoAnimator;
import com.umeng.common.ui.dialogs.UserReportDialog;
import com.umeng.common.ui.mvpview.MvpUserInfoView;
import com.umeng.common.ui.presenter.impl.UserInfoPresenter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.util.UserTypeUtil;
import com.umeng.common.ui.util.ViewFinder;
import com.umeng.common.ui.widgets.CommentEditText;
import com.umeng.common.ui.widgets.RoundImageView;
import com.umeng.common.ui.widgets.SquareImageView;


/**
 * 用户个人信息页面, 包含已发布的消息、已关注的话题、已关注的人三个fragment, 以及用户的头像、个人基本信息等.
 */
public abstract  class UserInfoBaseActivity extends BaseFragmentActivity implements OnClickListener,
        MvpUserInfoView {

//    /**
//     * 已发送Feed的Fragment
//     */
//    protected PostedFeedsFragment mPostedFragment = null;
//
//    /**
//     * 关注的好友Fragment
//     */
//    protected FollowedUserFragment mFolloweredUserFragment;
//
//    /**
//     * 粉丝Fragment
//     */
//    protected FansFragment mFansFragment;

    protected TextView mUserNameTv;
    private RoundImageView mHeaderImageView;
    protected ImageView mGenderImageView;
    protected ImageView mFollowToggleButton;
    /**
     * 该用户为传递进来的user，可能是好友、陌生人等身份
     */
    protected CommUser mUser;

    /**
     * 已经发布的消息标签, 用于切换Fragment
     */
    protected TextView mPostedTv;
    /**
     * 已经发布的消息数量标签
     */
    protected TextView mPostedCountTv;
    /**
     * 已经关注的用户标签, 用于切换Fragment
     */
    protected TextView mFollowedUserTv;

    /**
     * 已经关注的用户数量标签
     */
    protected TextView mFollowedUserCountTv;
    /**
     * 我的粉丝标签, 用于切换Fragment
     */
    protected TextView mFansTextView;
    /**
     * 我的fans用户数量标签
     */
    protected TextView mFansCountTextView;

    protected CommentEditText mCommentEditText;

    protected View mCommentLayout;

    protected int mSelectedColor = Color.BLUE;
    /**
     * 相册TextView
     */
    protected TextView mAlbumTextView;
    /**
     * 话题TextView
     */
    protected TextView mTopicTextView;

    /**
     * 视图查找器,避免每次findViewById进行强转
     */
    protected ViewFinder mViewFinder;
    /**
     * 用户信息的Presenter
     */
    protected UserInfoPresenter mPresenter;

    protected View mHeaderView;
    protected View mTitleView;
    protected View messageBtn;
    /**
     * 举报用户的Dialog
     */
    protected UserReportDialog mReportDialog;

    protected View mTabCursor;
    protected int mScreenWidth;
    protected int mCurrentTab;
    protected LinearLayout typeContainer;
    protected SquareImageView mUserMedalImg;
    protected ImgDisplayOption mUserMedalImgOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GetLayout());
        mUser = getIntent().getExtras().getParcelable(Constants.TAG_USER);
        if (mUser == null) {
            return;
        }

        mUserMedalImgOption = new ImgDisplayOption();
        mPresenter = new UserInfoPresenter(this, this, mUser);
        initFragment();

        typeContainer = (LinearLayout) findViewById(ResFinder.getId("user_type_icon_container"));
        // 初始化UI
        initUIComponents();
        mPresenter.onCreate(savedInstanceState);
        // 设置用户信息View的显示内容
        setupUserInfo(mUser);
        initHeaderView();
        BroadcastUtils.registerFeedBroadcast(getApplicationContext(), mReceiver);
        BroadcastUtils.registerUserBroadcast(getApplicationContext(), mReceiver);

    }

    public abstract void initFragment();
    protected void initCommentView() {
        mCommentEditText = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_comment_edittext"));
        mCommentLayout = findViewById(ResFinder.getId("umeng_comm_commnet_edit_layout"));

        findViewById(ResFinder.getId("umeng_comm_comment_send_button")).setOnClickListener(this);
        mCommentEditText.setEditTextBackListener(new CommentEditText.EditTextBackEventListener() {

            @Override
            public boolean onClickBack() {
                hideCommentLayout();
                return true;
            }
        });
    }
    public abstract int GetLayout();
    /**
     * 隐藏评论布局</br>
     */
    protected void hideCommentLayout() {
        mCommentLayout.setVisibility(View.GONE);
        hideInputMethod(mCommentEditText);
    }

    @SuppressWarnings("deprecation")
    protected void initUIComponents() {
        // 设置Fragment
//        addFragment(ResFinder.getId("umeng_comm_user_info_fragment_container"),
//                mPostedFragment);

        // 选中的某个tab时的文字颜色
        mSelectedColor = ResFinder.getColor("umeng_comm_text_topic_light_color");

        // 初始化feed、好友、粉丝、back、设置的listener
        findViewById(ResFinder.getId("umeng_comm_posted_layout")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_follow_user_layout")).setOnClickListener(
                this);
        findViewById(ResFinder.getId("umeng_comm_my_fans_layout")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_title_back_btn")).setOnClickListener(this);

        // 举报用户的Dialog
        mReportDialog = new UserReportDialog(this);
        mReportDialog.setTargetUid(mUser.id);

        messageBtn = findViewById(ResFinder.getId("umeng_comm_title_chat_btn"));
        CommUser mLoginedUser = CommConfig.getConfig().loginedUser;
        if (mLoginedUser != null && !mLoginedUser.id.equals(mUser.id)
                && (mUser.permisson == Permisson.ADMIN
                || mUser.permisson == Permisson.SUPPER_ADMIN
                || mLoginedUser.permisson == Permisson.SUPPER_ADMIN
                || mLoginedUser.permisson == Permisson.ADMIN)) {
            messageBtn.setVisibility(View.VISIBLE);
            messageBtn.setOnClickListener(new LoginOnViewClickListener() {
                @Override
                protected void doAfterLogin(View v) {
                    Intent i = new Intent(UserInfoBaseActivity.this, MessageChatActivity.class);
                    i.putExtra("uid", mUser.id);
                    i.putExtra("userName", mUser.name);
                    UserInfoBaseActivity.this.startActivity(i);
                }
            }
//            {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(UserInfoActivity.this, MessageChatActivity.class);
//                    i.putExtra("uid", mUser.id);
//                    i.putExtra("userName", mUser.name);
//                    UserInfoActivity.this.startActivity(i);
//                }
//            }
            );
        } else {
            messageBtn.setVisibility(View.GONE);
        }

        View settingButton = findViewById(ResFinder.getId("umeng_comm_title_more_btn"));
        if (mUser.permisson == Permisson.ADMIN){
            settingButton.setVisibility(View.INVISIBLE);
        }
        settingButton.setOnClickListener(new LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                mReportDialog.show();
            }
        });
        // 如果是用户自己，则不显示设置菜单按钮【目前菜单只有举报功能，即自己不能举报自己】
        if (mUser.id.equals(CommConfig.getConfig().loginedUser.id)
                || mUser.permisson == Permisson.SUPPER_ADMIN) {
            settingButton.setVisibility(View.GONE);
            if(messageBtn.getVisibility() == View.VISIBLE){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)messageBtn.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        }

        //
        mPostedTv = mViewFinder.findViewById(ResFinder.getId("umeng_comm_posted_msg_tv"));
        mPostedTv.setTextColor(mSelectedColor);

        //
        mPostedCountTv = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_posted_count_tv"));
        mPostedCountTv.setTextColor(mSelectedColor);

        mFollowedUserTv = mViewFinder.findViewById(ResFinder.getId(
                "umeng_comm_followed_user_tv"));
        mFollowedUserCountTv = mViewFinder.findViewById(ResFinder.getId(
                "umeng_comm_follow_user_count_tv"));

        mFansTextView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_my_fans_tv"));
        mFansCountTextView = mViewFinder.findViewById(ResFinder.getId(
                "umeng_comm_fans_count_tv"));
        // 昵称
        mUserNameTv = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_name_tv"));
        mUserNameTv.setText(mUser.name);

        mHeaderImageView = mViewFinder.findViewById(ResFinder.getId(
                "umeng_comm_user_header"));
        mHeaderImageView.setBackGroundColor(ResFinder.getColor("umeng_comm_color_transparent"));

        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(mUser.gender);
        mHeaderImageView.setImageUrl(mUser.iconUrl, option);
        // 用户性别
        mGenderImageView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_gender"));

        // 关注按钮
        mFollowToggleButton = mViewFinder.findViewById(ResFinder.getId(
                "umeng_comm_user_follow"));
//        mFollowToggleButton.setOnClickListener(new LoginOnViewClickListener() {
//
//            @Override
//            protected void doAfterLogin(View v) {
//                mFollowToggleButton.setClickable(false);
//                // true为选中状态为已关注，此时显示文本为“取消关注”；false代表未关注，此时显示文本为“关注”
//                if (!mUser.isFollowed) {
//                    mPresenter.followUser(mResultListener);
//                } else {
//                    mPresenter.cancelFollowUser(mResultListener);
//                }
//            }
//        });

        mAlbumTextView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_albums_tv"));
//        mAlbumTextView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                jumpToActivityWithUid(AlbumActivity.class);
//            }
//        });
        mTopicTextView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_user_topic_tv"));
//        mTopicTextView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                jumpToActivityWithUid(FollowedTopicActivity.class);
//            }
//        });

        // 用户自己(在未登录的情况下，点击设置跳转到登录，此时传递进来的uid是空的情况)，隐藏关注按钮，显示设置按钮
        // // 如果是超级管理员且已经关注，则隐藏取消关注按钮
        if (isHideFollowStatus()) {
            mFollowToggleButton.setVisibility(View.GONE);
        }
        initCommentView();

        mScreenWidth = DeviceUtils.getScreenSize(getApplicationContext()).x;
        mTabCursor = findViewById(ResFinder.getId("umeng_comm_tab_cursor"));

        mUserMedalImg = mViewFinder.findViewById(ResFinder.getId("user_comm_user_medal_img"));
        ListenerSet();
    }
    protected abstract void ListenerSet();
    protected boolean isHideFollowStatus() {
        if (TextUtils.isEmpty(mUser.id)) {
            return true;
        }
        CommUser loginUser = CommConfig.getConfig().loginedUser;
        if (mUser.id.equals(loginUser.id)) { // 如果是用户自己，则不显示关注/取消关注
            return true;
        }
        // 如果是超级管理员且已经被关注，则显示关注/取消关注
        if (mUser.permisson == Permisson.SUPPER_ADMIN && mUser.isFollowed) {
            return true;
        }
        return false;
    }

    protected void initHeaderView() {
        mHeaderView = findViewById(ResFinder.getId("umeng_comm_portrait_layout"));
        mHeaderView.getViewTreeObserver().addOnGlobalFocusChangeListener(mChangeListener);
        mTitleView = findViewById(ResFinder.getId("umeng_comm_title_layout"));
    }

    protected CustomAnimator mCustomAnimator = new UserInfoAnimator();
    protected OnResultListener mListener = new OnResultListener() {

        @Override
        public void onResult(int status) {
            if (status == 1) {// dismiss
                mCustomAnimator.startDismissAnimation(mHeaderView);
            } else if (status == 0) { // show
                mCustomAnimator.startShowAnimation(mHeaderView);
            }
        }
    };

    protected OnGlobalFocusChangeListener mChangeListener = new OnGlobalFocusChangeListener() {

        @Override
        public void onGlobalFocusChanged(View oldFocus, View newFocus) {
            int pos = mHeaderView.getHeight() - mHeaderView.getPaddingTop()
                    + mTitleView.getHeight() / 2;
            mCustomAnimator.setStartPosition(pos);
            mHeaderView.getViewTreeObserver().removeOnGlobalFocusChangeListener(mChangeListener);
        }
    };

    protected void jumpToActivityWithUid(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.putExtra(Constants.USER_ID_KEY, mUser.id);
        startActivity(intent);
    }

    /**
     * 避免对此点击，在回调中将状态设置为可点击状态~
     */
    protected OnResultListener mResultListener = new OnResultListener() {

        @Override
        public void onResult(int status) {
            mFollowToggleButton.setClickable(true);
        }
    };

    @Override
    public abstract void onClick(View v) ;

    protected void moveTabCurosr(int position){
        int endPosition = 0;
        int startPosition = 0;
        int distance = (mScreenWidth - CommonUtils.dip2px(getApplicationContext(),34 * 2 + 32 * 3)) / 2;

        switch (mCurrentTab){
            case 0:
                startPosition = 0;
                break;
            case 1:
                startPosition = CommonUtils.dip2px(getApplicationContext(), 32) + distance;
                break;
            case 2:
                startPosition = CommonUtils.dip2px(getApplicationContext(), 32 * 2) + distance * 2;
                break;
            default:
                break;
        }

        switch (position){
            case 0:
                endPosition = 0;
                break;
            case 1:
                endPosition = CommonUtils.dip2px(getApplicationContext(), 32) + distance;
                break;
            case 2:
                endPosition = CommonUtils.dip2px(getApplicationContext(), 32 * 2) + distance * 2;
                break;
            default:
                break;
        }
        mCurrentTab = position;
        TranslateAnimation animation = new TranslateAnimation(startPosition, endPosition, 0, 0);
        animation.setDuration(200);//设置动画持续时间
        animation.setFillAfter(true);
        mTabCursor.startAnimation(animation);
    }

    /**
     * 设置用户相关的信息 </br>
     *
     * @param user
     */
    @Override
    public void setupUserInfo(CommUser user) {
        if (!user.id.equals(mUser.id)) {
            return;
        }
        mUser = user;
        mUserNameTv.setText(user.name);
        if (user.gender == Gender.MALE) {
            mGenderImageView.setImageDrawable(ResFinder.getDrawable("umeng_comm_user_info_male"));
        } else if (user.gender == Gender.FEMALE) {
            mGenderImageView.setImageDrawable(ResFinder.getDrawable("umeng_comm_user_info_female"));
        }
        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
        mHeaderImageView.setImageUrl(user.iconUrl, option);
//        mHeaderImageView.setImageDrawable(ResFinder.getDrawable("morentuf"));
        // 设置用户头像
//        if (TextUtils.isEmpty(user.iconUrl)){
//            mHeaderImageView.setImageDrawable(ResFinder.getDrawable("morentuf"));
//        }else {
//            mHeaderImageView.setImageUrl(user.iconUrl);
//        }


        ImageLoaderManager.getInstance().getCurrentSDK().resume();
        if (isHideFollowStatus()) {
            mFollowToggleButton.setVisibility(View.GONE);
        } else {
            mFollowToggleButton.setVisibility(View.VISIBLE);
            updataFollowBtnState();
        }

        TextView mScoreTextView = (TextView)findViewById(ResFinder.getId("umeng_comm_user_jifen_tv"));
        StringBuffer str = new StringBuffer(ResFinder.getString("umeng_comm_user_socre"));
        str.append(CommonUtils.getLimitedCount(mUser.point));
        mScoreTextView.setText(str.toString());

//        if(user.medals != null && !user.medals.isEmpty()
//                && !TextUtils.isEmpty(user.medals.get(0).imgUrl)){
//            mUserMedalImg.setImageUrl(user.medals.get(0).imgUrl, mUserMedalImgOption);
//            mUserMedalImg.setVisibility(View.VISIBLE);
//        }else{
//            mUserMedalImg.setVisibility(View.GONE);
//        }
        UserTypeUtil.SetUserType(this, user, typeContainer);
    }

    protected void updataFollowBtnState(){
        boolean isFollowed = mUser.isFollowed;
        boolean isFollowing = mUser.isFollowingMe;
        if (isFollowed && isFollowing) {
            mFollowToggleButton.setImageDrawable(ResFinder.getDrawable("xianghu"));
        } else if (isFollowed) {
            mFollowToggleButton.setImageDrawable(ResFinder.getDrawable("yiguanzhu"));
        } else {
            mFollowToggleButton.setImageDrawable(ResFinder.getDrawable("jiaguanzhu"));
        }
    }

    /**
     * 修改文本颜色 </br>
     */
    protected abstract void changeSelectedText() ;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCommentLayout.isShown()) {
            mCommentLayout.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置文本颜色</br>
     *
     * @param postedColor 已发送feed文本颜色
     * @param followColor 关注文本颜色
     * @param fansColor   粉丝文本颜色
     */
    protected void changeTextColor(int postedColor, int followColor, int fansColor) {
        mPostedTv.setTextColor(postedColor);
        mPostedCountTv.setTextColor(postedColor);
        mFollowedUserTv.setTextColor(followColor);
        mFollowedUserCountTv.setTextColor(followColor);
        mFansTextView.setTextColor(fansColor);
        mFansCountTextView.setTextColor(fansColor);
    }

    /**
     * 关注用户数的回调函数。在加载缓存或者下拉刷新时，可能需要更新显示的用户数字。
     */
    protected OnResultListener mFollowListener = new OnResultListener() {

        @Override
        public void onResult(final int status) {
            if (mPresenter.isUpdateFollowUserCountTextView()) {
                CommonUtils.runOnUIThread(UserInfoBaseActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        mFollowedUserCountTv.setText(String.valueOf(status));
                    }
                });
            }
        }
    };

    /**
     * 粉丝数的回调函数。在加载缓存或者下拉刷新时，可能需要更新显示的用户数字。
     */
    protected OnResultListener mFansListener = new OnResultListener() {

        @Override
        public void onResult(final int status) {
            if (mPresenter.isUpdateFansCountTextView()) {
                CommonUtils.runOnUIThread(UserInfoBaseActivity.this, new Runnable() {

                    @Override
                    public void run() {
                        mFansCountTextView.setText(String.valueOf(status));
                    }
                });
            }
        }
    };

    @Override
    public void setToggleButtonStatus(boolean status) {
        mUser.isFollowed = status;
        updataFollowBtnState();
    }

    @Override
    public void updateFansTextView(int count) {
        mFansCountTextView.setText(String.valueOf(CommonUtils.getLimitedCount(count)));
    }

    @Override
    public void updateFeedTextView(int count) {
        mUser.feedCount = count;
        mPostedCountTv.setText(String.valueOf(CommonUtils.getLimitedCount(count)));
    }

    @Override
    public void updateFollowTextView(int count) {
        mFollowedUserCountTv.setText(String.valueOf(CommonUtils.getLimitedCount(count)));
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        BroadcastUtils.unRegisterBroadcast(getApplicationContext(), mReceiver);
        super.onDestroy();
    }

    /**
     * 数据同步处理
     */
    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {

        public void onReceiveFeed(Intent intent) {// 发送or删除时
            FeedItem feedItem = getFeed(intent);
            if (feedItem == null || !CommonUtils.isMyself(mUser)) {
                return;
            }

            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_POST == type) {
                updateFeedTextView(++mUser.feedCount);
            }
        }

        @Override
        public void onReceiveUser(Intent intent) {
            CommUser newUser = getUser(intent);
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            boolean follow = true;
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_FOLLOW) {
                follow = true;
            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_CANCEL_FOLLOW) {
                follow = false;
            }
//            for (CommUser user : users) {
//                if (user.id.equals(newUser.id)) {
//                    user.isFollowed = follow;
//                    user.extraData.putBoolean(Constants.IS_FOCUSED, follow);
//                    break;
//                }
//            }
           ReceiverComplete(newUser,follow);
        }
    };
    protected abstract void ReceiverComplete(CommUser user ,boolean follow);
}
