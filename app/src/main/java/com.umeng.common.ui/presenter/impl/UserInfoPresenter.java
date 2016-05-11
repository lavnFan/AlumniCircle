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

package com.umeng.common.ui.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.ProfileResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpUserInfoView;
import com.umeng.common.ui.presenter.BaseActivityPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户信息页面的Presenter
 */
public class UserInfoPresenter implements BaseActivityPresenter {
//    private static final String USER_INFO = "umeng_comm_user_info";
    private MvpUserInfoView mUserInfoView;
    private Activity mActivity;
    private CommunitySDK mSdkImpl;
    private List<Topic> mFollowTopics = new ArrayList<Topic>();
    private CommUser mUser;

    private int mFeedsCount;
    private int mFollowUserCount;
    private int mFansCount;

    private boolean mHasLoadDataFromService;

    public UserInfoPresenter(Activity activity, MvpUserInfoView userInfoView, CommUser user) {
        this.mActivity = activity;
        this.mUserInfoView = userInfoView;
        this.mUser = user;
        this.mSdkImpl = CommunityFactory.getCommSDK(activity);
    }

    @Override
    public void onCreate(Bundle bundle) {
        registerBroadcast();
        loadTopicFromDB();
        initUserInfoFromSharePref();
//        loadTopicsFromServer();
        loadUserFromDB();
        fetchUserProfile();
        findFollowedByMe();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        BroadcastUtils.unRegisterBroadcast(mActivity, mReceiver);
    }

    public void loadTopicFromDB() {
        DatabaseAPI.getInstance().getTopicDBAPI()
                .loadTopicsFromDB(mUser.id, new SimpleFetchListener<List<Topic>>() {
                    @Override
                    public void onComplete(List<Topic> result) {
                        if (CommonUtils.isActivityAlive(mActivity)) {
                            mFollowTopics.clear();
                            mFollowTopics.addAll(result);
//                            mUserInfoView.cleanAndUpdateTopicView(result);
                        }
                    }
                });

    }

    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveTopic(Intent intent) {
            if (!CommonUtils.isMyself(mUser)) {
                return;
            }
            Topic topic = getTopic(intent);
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_TOPIC_FOLLOW) {
                mFollowTopics.add(topic);
            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_TOPIC_CANCEL_FOLLOW) {
                mFollowTopics.remove(topic);
            }
        }

        public void onReceiveUser(Intent intent) {
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_UPDATE) {
                CommUser user = getUser(intent);
                if (user != null) {
                    mUserInfoView.setupUserInfo(user);
                    mUserInfoView.updateFansTextView(mFansCount);
                    mUserInfoView.updateFeedTextView(mFeedsCount);
                    mUserInfoView.updateFollowTextView(mFollowUserCount);
                }
            }
        }

        public void onReceiveCount(Intent intent) {
            if (!CommonUtils.isMyself(mUser)) {
                return;
            }

            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            int count = getCount(intent);
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_COUNT_USER) {
                if (Math.abs(count) <= 1) {// follow or unFollow 情况
                    mFollowUserCount += count;
                    mUserInfoView.updateFollowTextView(mFollowUserCount);
                } else if (mFollowUserCount < 1) { // 从DB重加载的情况，可能加载速度慢于网络
                    mFollowUserCount = count;
                    mUserInfoView.updateFollowTextView(mFollowUserCount);
                }
            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_COUNT_FEED) {
                if (Math.abs(count) <= 1) { // post or delete feed
                    mFeedsCount += count;
                    mUserInfoView.updateFeedTextView(mFeedsCount);
                } else if (mFeedsCount < 1) { // 从DB重加载的情况，可能加载速度慢于网络
                    mFeedsCount = count;
                    mUserInfoView.updateFeedTextView(count);
                }
            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_COUNT_FANS) {
                if (Math.abs(count) <= 1) {
                    mFansCount += count;
                    mUserInfoView.updateFansTextView(count);
                } else if (mFansCount < 1) {
                    mFansCount = count;
                    mUserInfoView.updateFansTextView(count);
                }
            }
        }
    };

    private void registerBroadcast() {
        BroadcastUtils.registerTopicBroadcast(mActivity, mReceiver);
        BroadcastUtils.registerUserBroadcast(mActivity, mReceiver);
        BroadcastUtils.registerCountBroadcast(mActivity, mReceiver);
    }

    /**
     * 在删除feed的时候，需要将该数字-1</br>
     * 
     * @param count
     */
    public void decreaseFeedCount(int count) {
        --mFeedsCount;
    }

    private void initUserInfoFromSharePref() {
        // 此处分三个查询。
        DatabaseAPI.getInstance().getFansDBAPI()
                .queryFansCount(mUser.id, new SimpleFetchListener<Integer>() {

                    @Override
                    public void onComplete(Integer count) {
                        if (mFansCount == 0 && count > 0) {
                            mFansCount = count;
                            mUserInfoView.updateFansTextView(mFansCount);
                        }
                    }
                });

        DatabaseAPI.getInstance().getFollowDBAPI()
                .queryFollowCount(mUser.id, new SimpleFetchListener<Integer>() {

                    @Override
                    public void onComplete(Integer count) {
                        if (mFollowUserCount == 0 && count > 0) {
                            mFollowUserCount = count;
                            mUserInfoView.updateFollowTextView(mFollowUserCount);
                        }
                    }
                });

        DatabaseAPI.getInstance().getFeedDBAPI()
                .queryFeedCount(mUser.id, new SimpleFetchListener<Integer>() {

                    @Override
                    public void onComplete(Integer count) {
                        if (mFeedsCount == 0 && count > 0) {
                            mFeedsCount = count;
                            mUserInfoView.updateFeedTextView(mFeedsCount);
                        }
                    }
                });
    }

    private void loadUserFromDB(){
        DatabaseAPI.getInstance().getUserDBAPI().loadUserFromDB(mUser.id, new SimpleFetchListener<CommUser>() {

            @Override
            public void onComplete(CommUser user) {
                if (user != null && !mHasLoadDataFromService) {
                    mUserInfoView.setToggleButtonStatus(user.isFollowed);
                    if (!TextUtils.isEmpty(user.id)) {
                        // feeds, fans, follow user个数
                        mFeedsCount = user.feedCount;
                        mFollowUserCount = user.followCount;
                        mFansCount = user.fansCount;
                        // 更新相关的现实VIew
                        mUserInfoView.setupUserInfo(user);
                        mUserInfoView.updateFansTextView(mFansCount);
                        mUserInfoView.updateFeedTextView(mFeedsCount);
                        mUserInfoView.updateFollowTextView(mFollowUserCount);
                    }
                }
            }
        });
    }

    /**
     * 获取用户信息并设置</br>
     */
    private void fetchUserProfile() {
        mSdkImpl.fetchUserProfile(mUser.id, new FetchListener<ProfileResponse>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(ProfileResponse response) {
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                mHasLoadDataFromService = true;
                mUserInfoView.setToggleButtonStatus(response.hasFollowed);
                CommUser user = response.result;

                Log.d("", "### 用户信息 : " + response.toString());
                if (!TextUtils.isEmpty(user.id)) {
//                    saveUserInfo(user);
                    // feeds, fans, follow user个数
                    mFeedsCount = response.mFeedsCount;
                    mFollowUserCount = response.mFollowedUserCount;
                    mFansCount = response.mFansCount;
                    // 更新相关的现实VIew
                    mUserInfoView.setupUserInfo(user);
                    mUserInfoView.updateFansTextView(mFansCount);
                    mUserInfoView.updateFeedTextView(mFeedsCount);
                    mUserInfoView.updateFollowTextView(mFollowUserCount);
                }
                DatabaseAPI.getInstance().getUserDBAPI().saveUserInfoToDB(user);
            }
        });
    }

//    private void saveUserInfo(CommUser user){
//        if (mActivity == null) {
//            return;
//        }
//        SharedPreferences preferences = mActivity.getSharedPreferences(USER_INFO + Constants.UMENG_APPKEY,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(HttpProtocol.ID_KEY, user.id);
//        editor.putInt(HttpProtocol.AGE_KEY, user.age);
//        editor.putString(HttpProtocol.SOURCE_KEY, user.source.toString());
//        editor.putInt(HttpProtocol.LEVEL_KEY, user.level);
//        editor.putString(HttpProtocol.LEVEL_TITLE_KEY, user.levelTitle);
//        editor.putString(HttpProtocol.OPEN_UID_KEY, user.token);
//        editor.putInt(HttpProtocol.UNREAD_COUNT_KEY, user.unReadCount);
//        editor.putString(HttpProtocol.PERMISSIONS_KEY, permissionToString(user.subPermissions));
//        editor.commit();
//    }
    /**
     * 将权限转换成String</br>
     *
     * @param permissions
     * @return
     */
//    private static String permissionToString(List<CommUser.SubPermission> permissions) {
//        if (permissions == null || permissions.size() == 0) {
//            return "";
//        }
//        StringBuffer buffer = new StringBuffer();
//        for (CommUser.SubPermission permission : permissions) {
//            buffer.append(permission.toString()).append(",");
//        }
//        buffer.deleteCharAt(buffer.length() - 1);
//        return buffer.toString();
//    }
    /**
     * 关注某个用户</br>
     * 
     * @param listener 被关注用户的id
     */
    public void followUser(final OnResultListener listener) {
        mSdkImpl.followUser(mUser, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if ( NetworkUtils.handleResponseComm(response) ) {
                    return ;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_success");
                    mUserInfoView.setToggleButtonStatus(true);
                    mUserInfoView.updateFansTextView(++mFansCount);
                    mUser.isFollowed = true;
                    DatabaseAPI.getInstance().getFollowDBAPI().follow(mUser);
                    BroadcastUtils.sendUserFollowBroadcast(mActivity, mUser);
                    BroadcastUtils.sendCountUserBroadcast(mActivity, 1);
                } else if ( response.errCode == ErrorCode.ERROR_USER_FOCUSED) {
                    mUserInfoView.setToggleButtonStatus(true);
                    mUser.isFollowed = true;
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_focused");
                } else {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_failed");
                    mUserInfoView.setToggleButtonStatus(false);
                    mUser.isFollowed = false;
                }
                listener.onResult(0);
            }
        });
    }

    /**
     * 取消关注某个用户</br>
     * 
     * @param listener 需要取消关注的用户的id
     */
    public void cancelFollowUser(final OnResultListener listener) {
        mSdkImpl.cancelFollowUser(mUser, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if ( NetworkUtils.handleResponseComm(response) ) {
                    return ;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_success");
                    mUserInfoView.setToggleButtonStatus(false);
                    mUser.isFollowed = false;
                    mUserInfoView.updateFansTextView(-- mFansCount > 0 ? mFansCount : 0);
                    DatabaseAPI.getInstance().getFollowDBAPI().unfollow(mUser);
                    // 发送取消关注的广播
                    BroadcastUtils.sendUserCancelFollowBroadcast(mActivity, mUser);
                    BroadcastUtils.sendCountUserBroadcast(mActivity, -1);
                    DatabaseAPI.getInstance().getFeedDBAPI().deleteFriendFeed(mUser.id);
                }else if (response.errCode == ErrorCode.ERROR_USER_NOT_FOCUSED) {
                    mUser.isFollowed = false;
                    mUserInfoView.setToggleButtonStatus(false);
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_not_focused");
                } else {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_failed");
                    mUserInfoView.setToggleButtonStatus(true);
                    mUser.isFollowed = true;
                }
                listener.onResult(0);
            }
        });
    }

    /**
     * 检查该用户是否是当前登录用户的好友 [ 关注 ]
     */
    public void findFollowedByMe() {
        DatabaseAPI.getInstance().getFollowDBAPI().isFollowed(mUser.id, new
                SimpleFetchListener<List<CommUser>>() {
                    @Override
                    public void onComplete(List<CommUser> results) {
                        // 确保activity没有被销毁
                        if (!CommonUtils.isActivityAlive(mActivity)) {
                            return;
                        }

                        if (!CommonUtils.isListEmpty(results)) {
                            mUserInfoView.setToggleButtonStatus(true);
                        } else {
                            mUserInfoView.setToggleButtonStatus(false);
                        }
                    }
                });

    }

    /**
     * 是否更新关注用户文本.在获取缓存数据的时候调用</br>
     * 
     * @return
     */
    public boolean isUpdateFollowUserCountTextView() {
        return mFollowUserCount == 0;
    }

    /**
     * 是否更新粉丝文本.在获取缓存数据的时候调用</br>
     * 
     * @return
     */
    public boolean isUpdateFansCountTextView() {
        return mFansCount == 0;
    }

}
