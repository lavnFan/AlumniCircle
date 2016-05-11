/**
 * 
 */

package com.umeng.common.ui.presenter.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.FansResponse;
import com.umeng.comm.core.nets.responses.UsersResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpActiveUserFgView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.List;


/**
 * 
 */
public class ActiveUserFgPresenter extends BaseFragmentPresenter<List<CommUser>> {

    protected MvpActiveUserFgView mActiveUserFgView;
    private Topic mTopic;
    protected String mNextPageUrl;
//    private boolean hasRefresh;

    private boolean isRegisterLoginReceiver;

    public ActiveUserFgPresenter(MvpActiveUserFgView activeUserFgView, Topic topic) {
        this.mActiveUserFgView = activeUserFgView;
        this.mTopic = topic;
    }

    public ActiveUserFgPresenter(MvpActiveUserFgView activeUserFgView) {
        this.mActiveUserFgView = activeUserFgView;
    }

    public ActiveUserFgPresenter(MvpActiveUserFgView activeUserFgView, boolean isRegisterLoginReceiver) {
        this.mActiveUserFgView = activeUserFgView;
        this.isRegisterLoginReceiver = isRegisterLoginReceiver;
    }

    @Override
    public void attach(Context context) {
        super.attach(context);
        BroadcastUtils.registerUserBroadcast(mContext, mReceiver);
        if(isRegisterLoginReceiver){
            registerLoginSuccessBroadcast();
        }
    }

    @Override
    public void detach() {
        BroadcastUtils.unRegisterBroadcast(mContext, mReceiver);
        if(isRegisterLoginReceiver){
            mContext.unregisterReceiver(mLoginReceiver);
        }
        super.detach();
    }

    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchActiveUsers(mTopic.id, new FetchListener<UsersResponse>() {

            @Override
            public void onStart() {
                mActiveUserFgView.onRefreshStart();
            }

            @Override
            public void onComplete(UsersResponse response) {
                mActiveUserFgView.onRefreshEnd();
                dealResult(response, true);
            }
        });
    }

    @Override
    public void loadDataFromDB() {

    }

    @Override
    public void loadMoreData() {
        mActiveUserFgView.onRefreshEnd();
    }

    /**
     * 处理结果集数据</br>
     * 
     * @param response
     * @param fromRefresh
     */
    void dealResult(FansResponse response, boolean fromRefresh) {
        if (NetworkUtils.handleResponseComm(response)
                || NetworkUtils.handResponseWithDefaultCode(response)) {
            return ;
        }
        dealNextpageUrl(response.nextPageUrl, fromRefresh);
        List<CommUser> users = response.result;
        if (CommonUtils.isListEmpty(users)) {
            // 加载用户为空
//            ToastMsg.showShortMsgByResName("umeng_comm_no_recommend_user");
            if(mActiveUserFgView.getBindDataSource().isEmpty()){
                mActiveUserFgView.showEmptyView();
            }else{
                mActiveUserFgView.hideEmptyView();
            }
            return;
        }

        List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
        dataSource.removeAll(users);
        if (fromRefresh) {
            dataSource.addAll(0, users);
        } else {
            dataSource.addAll(users);
        }
        mActiveUserFgView.notifyDataSetChanged();
        mActiveUserFgView.onRefreshEnd();
        if(mActiveUserFgView.getBindDataSource().isEmpty()){
            mActiveUserFgView.showEmptyView();
        }else{
            mActiveUserFgView.hideEmptyView();
        }
        mActiveUserFgView.setIsVisitBtn(response.isVisit);
    }

    private void dealNextpageUrl(String url, boolean fromRefersh) {
        if (fromRefersh && TextUtils.isEmpty(mNextPageUrl)) {
//            hasRefresh = true;
            mNextPageUrl = url;
        } else if (!fromRefersh) {
            mNextPageUrl = url;
        }
    }

    /**
     * 关注某个好友</br>
     * 
     * @param user
     */
    public void followUser(final CommUser user, final ToggleButton toggleButton) {
        if (isMySelf(user)) {
            toggleButton.setChecked(!toggleButton.isChecked());
            return;
        }
        mCommunitySDK.followUser(user, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_success");
                    toggleButton.setChecked(true);
                    user.fansCount += 1;
                    DatabaseAPI.getInstance().getFollowDBAPI().follow(user);
                    // 改变状态
                    List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
                    int Index = dataSource.indexOf(user);
                    dataSource.get(Index).extraData.putBoolean(Constants.IS_FOCUSED, true);
//                    mActiveUserFgView.notifyDataSetChanged();

                    // 发送关注用户的广播
                    BroadcastUtils.sendUserFollowBroadcast(mContext, user);
                    BroadcastUtils.sendCountUserBroadcast(mContext, 1);
                    return;
                }
                if (response.errCode == ErrorCode.ERROR_USER_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_focused");
                    user.isFollowed = true;
                    toggleButton.setChecked(true);
                    return;
                }

                ToastMsg.showShortMsgByResName("umeng_comm_follow_user_failed");
                toggleButton.setChecked(false);
            }
        });
    }

    /**
     * 取消关注某个好友</br>
     * 
     * @param user
     */
    public void cancelFollowUser(final CommUser user, final ToggleButton toggleButton) {
        if (isMySelf(user)) {
            toggleButton.setChecked(!toggleButton.isChecked());
            return;
        }
        mCommunitySDK.cancelFollowUser(user, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if ( NetworkUtils.handleResponseComm(response) ) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_failed");
                    toggleButton.setChecked(true);
                    return ;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_success");
                    toggleButton.setChecked(false);
                    user.fansCount -= 1;
                    DatabaseAPI.getInstance().getFollowDBAPI().unfollow(user);
                    // 改变状态
                    int Index = mActiveUserFgView.getBindDataSource().indexOf(user);
                    mActiveUserFgView.getBindDataSource().get(Index).extraData.putBoolean(
                            Constants.IS_FOCUSED,
                            false);
                    mActiveUserFgView.notifyDataSetChanged();

                    // 发送取消关注的广播
                    BroadcastUtils.sendUserCancelFollowBroadcast(mContext, user);
                    BroadcastUtils.sendCountUserBroadcast(mContext, -1);
                    DatabaseAPI.getInstance().getFeedDBAPI().deleteFriendFeed(user.id);
                    return;
                }
                if (response.errCode == ErrorCode.ERROR_USER_NOT_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_not_focused");
                    user.isFollowed = false;
                    toggleButton.setChecked(false);
                    return;
                }

                ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_failed");
                toggleButton.setChecked(true);
            }
        });
    }

    private boolean isMySelf(CommUser user) {
        if (user.id.equals(CommConfig.getConfig().loginedUser.id)) {
            ToastMsg.showShortMsgByResName("umeng_comm_no_follow_unfollow_myself");
            return true;
        }
        return false;
    }

    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveUser(Intent intent) {
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            CommUser user = getUser(intent);
            List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
            int index = dataSource.indexOf(user);
            if (index < 0) {
                return;
            }
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_FOLLOW) {
                dataSource.get(index).extraData
                        .putBoolean(Constants.IS_FOCUSED, true);
            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_CANCEL_FOLLOW) {
                dataSource.get(index).extraData.putBoolean(Constants.IS_FOCUSED,
                        false);
            }
            mActiveUserFgView.notifyDataSetChanged();
        }
    };

    public void setNextPageUrl(String url) {
        mNextPageUrl = url;
    }

    /**
     * 注册登录成功时的广播</br>
     */
    private void registerLoginSuccessBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_LOGIN_SUCCESS);
        mContext.registerReceiver(mLoginReceiver, filter);
    }

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mActiveUserFgView.onUserLogin();
        }
    };
}
