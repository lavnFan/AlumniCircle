package com.umeng.common.ui.presenter.impl;

import android.location.Location;
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.NearbyUsersResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.common.ui.mvpview.MvpFollowedUserView;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.List;

/**
 * Created by umeng on 16/3/22.
 */
public class NearbyUserPresenter extends FollowedUserFgPresenter {


    public NearbyUserPresenter(MvpFollowedUserView followedUserView, String uId) {
        super(followedUserView, uId);
    }

    @Override
    public void loadDataFromServer() {
        LocationSDKManager.getInstance().getCurrentSDK().requestLocation(mContext, new Listeners.
                SimpleFetchListener<Location>() {

            @Override
            public void onComplete(Location result) {
                if (result != null && result.getLongitude() != 0 && result.getLatitude() != 0) {
                    mCommunitySDK.fetchNearByUser(result.getLongitude(), result.getLatitude(),
                            mRefreshListener);
                }else{
                    mRefreshListener.onComplete(null);
                }
            }

        } );
    }

    @Override
    public void loadDataFromDB() {
    }

    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(nextPageUrl)) {
            mFollowedUserView.onRefreshEnd();
            return;
        }
        mCommunitySDK.fetchNextPageData(nextPageUrl, NearbyUsersResponse.class, mLoadMoreListener);
    }

    private Listeners.SimpleFetchListener<NearbyUsersResponse> mRefreshListener = new Listeners.
            SimpleFetchListener<NearbyUsersResponse>() {

        @Override
        public void onStart() {
            mFollowedUserView.onRefreshStart();
        }

        @Override
        public void onComplete(NearbyUsersResponse response) {
            if(response == null){
                mFollowedUserView.onRefreshEnd();
            }
            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response)) {
                mFollowedUserView.onRefreshEnd();
                if (response.errCode == ErrorCode.NO_ERROR) {
                    nextPageUrl = "";
                }
                return;
            }
            final List<CommUser> followedUsers = response.result;
            List<CommUser> dataSource = mFollowedUserView.getBindDataSource();
            dataSource.clear();
            dataSource.addAll(followedUsers);
            mFollowedUserView.notifyDataSetChanged();
            // 解析下一页地址
            parseNextpageUrl(response, true);
            mFollowedUserView.onRefreshEnd();
        }
    };

    private Listeners.SimpleFetchListener<NearbyUsersResponse> mLoadMoreListener = new Listeners.
            SimpleFetchListener<NearbyUsersResponse>() {

        @Override
        public void onComplete(NearbyUsersResponse response) {
            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response)) {
                if (response.errCode == ErrorCode.NO_ERROR) {
                    nextPageUrl = "";
                }
                mFollowedUserView.onRefreshEnd();
                return;
            }
            appendUsers(response.result);
            parseNextpageUrl(response, false);
            mFollowedUserView.onRefreshEnd();
        }
    };

    /**
     * 在其他页面对某个用户进行取消关注、关注之后需要从关注列表中移除或者添加
     *
     * @param user
     * @param type
     */
    protected void onUserFollowStateChange(CommUser user, BroadcastUtils.BROADCAST_TYPE type) {
        List<CommUser> dataSource = mFollowedUserView.getBindDataSource();
        boolean followedState;
        if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_FOLLOW) {
            followedState = true;
            mFollowDBAPI.follow(user);
        } else {
            followedState = false;
            mFollowDBAPI.unfollow(user);
        }
        int count = dataSource.size();
        for (int i = 0; i < count; i++){
            if(dataSource.get(i).id.equals(user.id)){
                CommUser tempUser = dataSource.get(i);
                tempUser.isFollowed = followedState;
                mFollowedUserView.notifyDataSetChanged();
                break;
            }
        }
    }


}
