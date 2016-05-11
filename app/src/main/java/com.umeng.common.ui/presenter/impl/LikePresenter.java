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

import android.text.TextUtils;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.SimpleResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpLikeView;
import com.umeng.common.ui.presenter.BasePresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.Iterator;


public class LikePresenter extends BasePresenter {

    private MvpLikeView mLikeViewInterface;
    private FeedItem mFeedItem;
    private OnResultListener mListener = null;

    private boolean mIsInProgress;

    public LikePresenter() {
    }

    public LikePresenter(MvpLikeView viewInterface) {
        mLikeViewInterface = viewInterface;
    }

    /**
     * 用户点赞操作</br>
     *
     * @param feedId 该条feed的id
     */
    public void postLike(final String feedId) {
        if (mIsInProgress) {
            return;
        }
        mIsInProgress = true;

        SimpleFetchListener<SimpleResponse> listener = new SimpleFetchListener<SimpleResponse>() {

            @Override
            public void onComplete(SimpleResponse response) {
                mIsInProgress = false;
                if (NetworkUtils.handleResponseComm(response)) {
                    return;
                }
                String tipStr = "umeng_comm_discuss_like_failed";
                if (!TextUtils.isEmpty(response.id)) {
                    likeSuccess(feedId, response.id);
                    tipStr = "umeng_comm_discuss_like_success";
                } else {
                    switch (response.errCode) {
                        case ErrorCode.NO_ERROR:
                            mFeedItem.isLiked = true;
                            if (mLikeViewInterface != null) {
                                mLikeViewInterface.like(feedId, true);
                                mFeedItem.likeCount += 1;
                                mLikeViewInterface.updateLikeView("");
                            }
                            tipStr = "umeng_comm_discuss_like_success";
                            break;

                        case ErrorCode.LIKED_CODE:
                            mFeedItem.isLiked = true;
                            if (mLikeViewInterface != null) {
                                mLikeViewInterface.like(feedId,true);
//                                mFeedItem.likeCount += 1;
                                mLikeViewInterface.updateLikeView("");
                            }
                            tipStr = "umeng_comm_liked";
                            break;

                        case ErrorCode.ERR_CODE_FEED_UNAVAILABLE:
                            tipStr = "umeng_comm_discuss_like_failed_deleted";
                            break;

                        case ErrorCode.ERR_CODE_FEED_LOCKED:
                            tipStr = "umeng_comm_discuss_like_failed_locked";
                            break;

                        case ErrorCode.ERR_CODE_USER_FORBIDDEN:
                            tipStr = "umeng_comm_discuss_like_failed_forbid";
                            break;

                        case ErrorCode.USER_FORBIDDEN_ERR_CODE:
                            tipStr = "umeng_comm_user_unusable";
                            break;
                    }
                }
                ToastMsg.showShortMsgByResName(tipStr);
            }
        };
        mCommunitySDK.postLike(feedId, listener);
    }

    public void postUnlike(final String feedId) {
        if (mIsInProgress) {
            return;
        }
        mIsInProgress = true;
        SimpleFetchListener<SimpleResponse> listener = new SimpleFetchListener<SimpleResponse>() {

            @Override
            public void onComplete(SimpleResponse response) {
                mIsInProgress = false;
                // 判断用户是否被禁言
                if (NetworkUtils.handleResponseComm(response)) {
                    return;
                }
                String tipStr = "umeng_comm_discuss_unlike_failed";
                if (ErrorCode.ERROR_COMMENT_LIKE_CANCELED == response.errCode) {
                    mFeedItem.isLiked = false;
                    mFeedItem.likeCount--;
                    String likeId = getLikeId();
                    removeLike();
                    BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem);
                    mDatabaseAPI.getFeedDBAPI().saveFeedToDB(mFeedItem);
                    mDatabaseAPI.getLikeDBAPI().deleteLikesFromDB(mFeedItem.id, likeId);
                    if (mLikeViewInterface != null) {
                        mLikeViewInterface.like(feedId, false);
                        mLikeViewInterface.updateLikeView("");
                    }
                    if (mListener != null) {
                        mListener.onResult(0);
                    }
                    tipStr = "umeng_comm_discuss_unlike_success";
                } else if (ErrorCode.NO_ERROR == response.errCode) {
                    mFeedItem.isLiked = false;
                    mFeedItem.likeCount--;
                    String likeId = getLikeId();
                    removeLike();
                    BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem);
                    mDatabaseAPI.getFeedDBAPI().saveFeedToDB(mFeedItem);
                    mDatabaseAPI.getLikeDBAPI().deleteLikesFromDB(mFeedItem.id, likeId);
                    if (mLikeViewInterface != null) {
                        mLikeViewInterface.like(feedId, false);
                        mLikeViewInterface.updateLikeView("");
                    }
                    if (mListener != null) {
                        mListener.onResult(0);
                    }
                    tipStr = "umeng_comm_discuss_unlike_success";
                }else if(response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE){
                    tipStr = "umeng_comm_discuss_unlike_failed_deleted";
                }else if(response.errCode == ErrorCode.ERR_CODE_FEED_LOCKED){
                    tipStr = "umeng_comm_discuss_unlike_failed_locked";
                }else if(response.errCode == ErrorCode.ERR_CODE_USER_FORBIDDEN){
                    tipStr = "umeng_comm_discuss_unlike_failed_forbid";
                }else if(response.errCode == ErrorCode.USER_FORBIDDEN_ERR_CODE){
                    tipStr = "umeng_comm_user_unusable";
                }
                ToastMsg.showShortMsgByResName(tipStr);
            }
        };
        mCommunitySDK.postUnLike(feedId, listener);
    }

    public void setFeedItem(FeedItem feedItem) {
        this.mFeedItem = feedItem;
    }

    public void setResultListener(OnResultListener listener) {
        this.mListener = listener;
    }

    private void likeSuccess(String feedId, String likeId) {
        Like like = new Like();
        CommUser likeUser = CommConfig.getConfig().loginedUser;
        like.id = likeId;
        like.creator = likeUser;
        mFeedItem.isLiked = true;

        // 通过feed id找到feed
        final FeedItem targetFeedItem = findFeedWithId(feedId);
        targetFeedItem.likes.add(like);
        targetFeedItem.isLiked = true;
        targetFeedItem.likeCount++;

        BroadcastUtils.sendFeedUpdateBroadcast(mContext, targetFeedItem);

        // 保存到数据库
        mDatabaseAPI.getLikeDBAPI().saveLikesToDB(targetFeedItem);
        mDatabaseAPI.getFeedDBAPI().saveFeedToDB(targetFeedItem);

        if (mLikeViewInterface != null) {
            mLikeViewInterface.like(feedId, true);
            mLikeViewInterface.updateLikeView("");
        }
        if (mListener != null) {
            mListener.onResult(0);
        }
    }

    /**
     * 获取当前用户点赞的likeid</br>
     *
     * @return
     */
    private String getLikeId() {
        String id = CommConfig.getConfig().loginedUser.id;
        Iterator<Like> iterator = mFeedItem.likes.iterator();
        while (iterator.hasNext()) {
            Like like = iterator.next();
            if (like.creator.id.equals(id)) {
                return like.id;
            }
        }
        return null;
    }

    /**
     * 移除自己点赞数据</br>
     */
    private void removeLike() {
        String id = CommConfig.getConfig().loginedUser.id;
        Iterator<Like> iterator = mFeedItem.likes.iterator();
        while (iterator.hasNext()) {
            Like like = iterator.next();
            if (like.creator.id.equals(id)) {
                iterator.remove();
                break;
            }
        }
    }

    private FeedItem findFeedWithId(String feedId) {
        return mFeedItem != null && mFeedItem.id.equals(feedId) ? mFeedItem : new FeedItem();
    }
}
