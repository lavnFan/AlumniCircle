package com.umeng.common.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.MessageCount;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.util.UserTypeUtil;
import com.umeng.common.ui.widgets.RoundImageView;


/**
 * Created by wangfei on 16/1/15.
 */
public abstract class FindBaseActivity extends BaseFragmentActivity implements View.OnClickListener {
    protected CommUser mUser;
    protected String mContainerClass;
    //    protected RecommendTopicBaseFragment mRecommendTopicFragment;
//    protected RecommendUserFragment mRecommendUserFragment;
//    protected FriendsFragment mFriendsFragment;
//    protected NearbyFeedFragment mNearbyFeedFragment;
//    protected FavoritesFragment mFavoritesFragment;
//    protected RealTimeFeedFragment mRealTimeFeedFragment;
    protected MessageCount mUnReadMsg;
    protected View mMsgBadgeView;
    protected View mNotifyBadgeView;
    protected LinearLayout typeContainer;
    protected Dialog processDialog;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getLayout();
        processDialog = new CustomCommomDialog(this,ResFinder.getString("umeng_comm_logining"));
        findViewById(ResFinder.getId("umeng_comm_title_back_btn")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_topic_recommend")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_user_recommend")).setOnClickListener(this);
        findViewById(ResFinder.getId("user_have_login")).setOnClickListener(this);
        findViewById(ResFinder.getId("user_haveno_login")).setOnClickListener(this);
//        findViewById(ResFinder.getId("umeng_comm_setting_recommend")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_friends")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_myfocustopic")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_mypics")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_favortes")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_notification")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_nearby_recommend")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_nearby_user")).setOnClickListener(this);
        findViewById(ResFinder.getId("umeng_comm_realtime")).setOnClickListener(this);
        typeContainer = (LinearLayout) findViewById(ResFinder.getId("user_type_icon_container"));
        // 右上角的通知
        findViewById(ResFinder.getId("umeng_comm_title_notify_btn")).setOnClickListener(this);
        // 未读消息红点
        mMsgBadgeView = findViewById(ResFinder.getId("umeng_comm_notify_badge_view"));
        mMsgBadgeView.setVisibility(View.GONE);

        // 未读系统通知的红点
        mNotifyBadgeView = findViewById(ResFinder.getId("umeng_comm_badge_view"));

        TextView textView = (TextView) findViewById(ResFinder.getId("umeng_comm_title_tv"));
        textView.setText(ResFinder.getString("umeng_comm_mine"));
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        parseIntentData();
        setupUnreadFeedMsgBadge();
        setupUnReadNotifyBadge();
        mUser = CommonUtils.getLoginUser(this);
        registerInitSuccessBroadcast();
    }

    protected void initUserInfo() {
        if (CommonUtils.isLogin(this)) {
            CommUser user = CommConfig.getConfig().loginedUser;
//            Log.e("xxxxxx","user="+user);
            findViewById(ResFinder.getId("user_have_login")).setVisibility(View.VISIBLE);
            findViewById(ResFinder.getId("user_haveno_login")).setVisibility(View.GONE);
            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon"))).setImageDrawable(ResFinder.getDrawable("morentuf"));
//            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon"))).setImageUrl(user.iconUrl);
            ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon"))).setImageUrl(user.iconUrl, option);
            ((TextView) findViewById(ResFinder.getId("user_name_tv"))).setText(user.name);

            StringBuffer content = new StringBuffer(ResFinder.getString("umeng_comm_my_fans"));
            content.append(" ").append(CommonUtils.getLimitedCount(user.fansCount));
            ((TextView) findViewById(ResFinder.getId("user_fanscount"))).setText(content.toString());

            content.delete(0, content.length());
            content.append(ResFinder.getString("umeng_comm_followed_user"));
            content.append(" ").append(CommonUtils.getLimitedCount(user.followCount));
            ((TextView) findViewById(ResFinder.getId("user_focuscount"))).setText(content.toString());

            content.delete(0, content.length());
            content.append(ResFinder.getString("umeng_comm_user_socre"));
            content.append(" ").append(CommonUtils.getLimitedCount(user.point));
            ((TextView) findViewById(ResFinder.getId("user_score"))).setText(content.toString());
        } else {
            findViewById(ResFinder.getId("user_haveno_login")).setVisibility(View.VISIBLE);
            findViewById(ResFinder.getId("user_have_login")).setVisibility(View.GONE);
            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon_nologin"))).setImageDrawable(ResFinder.getDrawable("morentuf"));
            ((TextView) findViewById(ResFinder.getId("user_name_tv_nologin"))).setText("立即登陆");
        }
        mUser = CommConfig.getConfig().loginedUser;
        UserTypeUtil.SetUserType(this, mUser, typeContainer);
    }

    protected void getLayout() {
        setContentView(ResFinder.getLayout("umeng_comm_find_layout"));
    }

    protected void parseIntentData() {
        mUser = getIntent().getExtras().getParcelable(Constants.TAG_USER);
        mContainerClass = getIntent().getExtras().getString(Constants.TYPE_CLASS);
        mUnReadMsg = CommConfig.getConfig().mMessageCount;
    }

    /**
     * 设置通知红点</br>
     */
    protected void setupUnReadNotifyBadge() {
//        if (mUnReadMsg.unReadNotice > 0) {
//            mNotifyBadgeView.setVisibility(View.INVISIBLE);
//        } else {
            mNotifyBadgeView.setVisibility(View.INVISIBLE);
//        }
    }

    /**
     * 设置消息数红点</br>
     */
    protected void setupUnreadFeedMsgBadge() {
        if (mUnReadMsg.unReadNotice > 0) {
            if (CommonUtils.isLogin(this)) {
                mMsgBadgeView.setVisibility(View.VISIBLE);
            } else {
                mMsgBadgeView.setVisibility(View.GONE);
            }
        } else {
            mMsgBadgeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_title_back_btn")) { // 返回事件
            finish();
        } else if (id == ResFinder.getId("umeng_comm_friends")) {
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {

                        if (stCode == 0) {
                            showFriendsFragment();
                        }
                        processDialog.dismiss();
                    }
                });
            } else {

                showFriendsFragment();
            }
        } else if (id == ResFinder.getId("umeng_comm_topic_recommend")) { // 话题推荐
            showRecommendTopic();
        } else if (id == ResFinder.getId("umeng_comm_user_recommend")) { // 用户推荐
            showRecommendUserFragment();
        } else if (id == ResFinder.getId("user_have_login")) { // 个人中心
            gotoUserInfoActivity();
        } else if (id == ResFinder.getId("user_haveno_login")) { // 个人中心
            CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                @Override
                public void onStart() {
                    processDialog.show();
                }

                @Override
                public void onComplete(int stCode, CommUser userInfo) {
                    initUserInfo();
                    processDialog.dismiss();
                }
            });
        } else if (id == ResFinder.getId("umeng_comm_nearby_recommend")) {
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {

                        processDialog.dismiss();
                        if (stCode == 0) {
                            showNearbyFeed();
                        }
                    }
                });
            } else {

                showNearbyFeed();
            }

        }else if (id == ResFinder.getId("umeng_comm_nearby_user")) {
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {

                        processDialog.dismiss();
                        if (stCode == 0) {
                            showNearByUser();
                        }
                    }
                });
            } else {
                showNearByUser();
            }

        }  else if (id == ResFinder.getId("umeng_comm_favortes")) {
            // 显示收藏的fragment
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        processDialog.dismiss();
                        if (stCode == 0) {
                            showFavoritesFeed();
                        }
                    }
                });
            } else {

                showFavoritesFeed();
            }
        } else if (id == ResFinder.getId("umeng_comm_notification")) {
            // 跳转到通知的Activity
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        processDialog.dismiss();
                        if (stCode == 0) {
                            gotoFeedNewMsgActivity();
                        }
                    }
                });
            } else {

                gotoFeedNewMsgActivity();
            }


        } else if (id == ResFinder.getId("umeng_comm_title_notify_btn")) { // 点击右上角的通知
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                final ProgressDialog mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setCanceledOnTouchOutside(false);
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        if (stCode == 0) {
                            Intent setting = new Intent(FindBaseActivity.this, SettingActivity.class);
                            setting.putExtra(Constants.TYPE_CLASS, mContainerClass);
                            startActivity(setting);
                        }
                        processDialog.dismiss();
                    }
                });
            } else {
                Intent setting = new Intent(this, SettingActivity.class);
                setting.putExtra(Constants.TYPE_CLASS, mContainerClass);
                startActivity(setting);
            }
        } else if (id == ResFinder.getId("umeng_comm_realtime")) { // 实时内容
            showRealTimeFeed();
        } else if (id == ResFinder.getId("umeng_comm_myfocustopic")) {
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        processDialog.dismiss();
                        if (stCode == 0) {
                            gotoMyFollowActivity();
                        }
                    }
                });
            } else {
                gotoMyFollowActivity();
            }

        } else if (id == ResFinder.getId("umeng_comm_mypics")) {
            if (!CommonUtils.isLogin(FindBaseActivity.this)) {
                CommunitySDKImpl.getInstance().login(FindBaseActivity.this, new LoginListener() {
                    @Override
                    public void onStart() {
                        processDialog.show();
                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        processDialog.dismiss();
                        if (stCode == 0) {
                            gotoMyPicActivity();
                        }
                    }
                });
            } else {

                gotoMyPicActivity();
            }

        }
    }

    protected abstract void gotoMyFollowActivity();

    protected abstract void gotoMyPicActivity();

    protected abstract void gotoNotificationActivity();

    protected abstract void gotoFeedNewMsgActivity();

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
        setupUnReadNotifyBadge();
        setupUnreadFeedMsgBadge();
    }

    /**
     * 跳转到用户中心Activity</br>
     */
    protected abstract void gotoUserInfoActivity();

    /**
     * 显示附件推荐Feed</br>
     */
    protected abstract void showNearbyFeed();

    /**
     * 显示附近用户Feed</br>
     */
    protected abstract void showNearByUser();

    /**
     * 显示实时内容的Fragment</br>
     */
    protected abstract void showRealTimeFeed();

    /**
     * 显示收藏Feed</br>
     */
    protected abstract void showFavoritesFeed();

    /**
     * 显示推荐话题的Dialog</br>
     */
    protected abstract void showRecommendTopic();

    /**
     * 隐藏发现页面，显示fragment</br>
     *
     * @param fragment
     */
    protected abstract void showCommFragment(Fragment fragment);

    /**
     * 隐藏fragment，显示发现页面</br>
     */
    protected void showFindPage(){
        initUserInfo();
    }

    /**
     * 显示朋友圈Fragment</br>
     */
    protected abstract void showFriendsFragment();

    /**
     * 显示推荐用户fragment</br>
     */
    protected abstract void showRecommendUserFragment();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && findViewById(ResFinder.getId("container")).getVisibility() == View.VISIBLE) {
            findViewById(ResFinder.getId("umeng_comm_find_base")).setVisibility(View.VISIBLE);
            findViewById(ResFinder.getId("container")).setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 注册登录成功时的广播</br>
     */
    protected void registerInitSuccessBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_INIT_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mInitConfigReceiver,
                filter);
    }

    protected BroadcastReceiver mInitConfigReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mUnReadMsg = CommConfig.getConfig().mMessageCount;
            setupUnReadNotifyBadge();
            setupUnreadFeedMsgBadge();
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInitConfigReceiver);
        super.onDestroy();
    }

}
