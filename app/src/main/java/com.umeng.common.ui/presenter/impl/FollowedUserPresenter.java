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

import android.content.Context;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.presenter.BasePresenter;
import com.umeng.common.ui.util.BroadcastUtils;


public class FollowedUserPresenter extends BasePresenter {


    private Context mContext;
    private CommunitySDK mSdkImpl;
    private boolean mIsInProgress;

    public FollowedUserPresenter(Context context) {
        this.mContext = context;
        this.mSdkImpl = CommunityFactory.getCommSDK(context);
    }

    /**
     * 关注某个用户</br>
     *
     * @param user 被关注用户的id
     */
    public void followUser(final CommUser user) {
        if (mIsInProgress) {
            return;
        }
        mIsInProgress = true;
        mSdkImpl.followUser(user, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                mIsInProgress = false;
                if (NetworkUtils.handleResponseComm(response)) {
                    return;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_success");
                    DatabaseAPI.getInstance().getFollowDBAPI().follow(user);
                    user.fansCount += 1;
                    BroadcastUtils.sendUserFollowBroadcast(mContext, user);
                    BroadcastUtils.sendCountUserBroadcast(mContext, 1);
                } else if (response.errCode == ErrorCode.ERROR_USER_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_focused");
                } else {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_failed");
                }
            }
        });
    }

    /**
     * 取消关注某个用户</br>
     *
     * @param user 需要取消关注的用户的id
     */
    public void cancelFollowUser(final CommUser user) {
        if (mIsInProgress) {
            return;
        }
        mIsInProgress = true;
        mSdkImpl.cancelFollowUser(user, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                mIsInProgress = false;
                if ( NetworkUtils.handleResponseComm(response) ) {
                    return ;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_success");
                    DatabaseAPI.getInstance().getFollowDBAPI().unfollow(user);
                    DatabaseAPI.getInstance().getFeedDBAPI().deleteFriendFeed(user.id);
                    user.fansCount -= 1;
                    // 发送取消关注的广播
                    BroadcastUtils.sendUserCancelFollowBroadcast(mContext, user);
                    BroadcastUtils.sendCountUserBroadcast(mContext, -1);
                }else if (response.errCode == ErrorCode.ERROR_USER_NOT_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_not_focused");
                } else {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_failed");
                }
            }
        });
    }
}
