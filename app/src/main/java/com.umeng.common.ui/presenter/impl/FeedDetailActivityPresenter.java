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
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners.CommListener;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.FeedItemResponse;
import com.umeng.comm.core.nets.responses.ForbidResponse;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.nets.responses.SimpleResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.dialogs.ConfirmDialog;
import com.umeng.common.ui.mvpview.MvpFeedDetailActivityView;
import com.umeng.common.ui.presenter.BasePresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.List;

public class FeedDetailActivityPresenter extends BasePresenter {

    MvpFeedDetailActivityView mActivityView;
    FeedItem mFeedItem;

    public FeedDetailActivityPresenter() {
    }

    public FeedDetailActivityPresenter(MvpFeedDetailActivityView view) {
        mActivityView = view;
        if (mActivityView instanceof Context) {
            mContext = (Context) mActivityView;
            mCommunitySDK = CommunityFactory.getCommSDK(mContext);
        } else {
            throw new NullPointerException("FeedDetailActivityPresenter构造函数的参数不是Context类型");
        }
    }

    public void setActivityView(MvpFeedDetailActivityView view) {
        this.mActivityView = view;
    }

    public void setFeedItem(FeedItem feedItem) {
        this.mFeedItem = feedItem;
    }

    public void showDeleteConfirmDialog() {
        ConfirmDialog.showDialog(mContext, ResFinder.getString("umeng_comm_delete_tips"),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whitch) {
                        deleteFeed();
                    }
                });
    }

    public void showReportUserConfirmDialog() {
        ConfirmDialog.showDialog(mContext, ResFinder.getString("umeng_comm_sure_spam"),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whitch) {
                        reportFeedUser();
                    }
                });
    }
    public void showReportConfirmDialog() {
        ConfirmDialog.showDialog(mContext, ResFinder.getString("umeng_comm_sure_spam"),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whitch) {
                        reportCurrentFeed();
                    }
                });
    }
    private Bundle mExtraData;

    public void setExtraData(Bundle extra) {
        this.mExtraData = new Bundle(extra);
        // if (mExtraData.containsKey(HttpProtocol.COMMENT_ID_KEY)) {
        // String commentId = mExtraData.getString(HttpProtocol.COMMENT_ID_KEY);
        // mExtraData.clear();
        // mExtraData.putString(HttpProtocol.COMMENT_ID_KEY, commentId);
        // // 表示该请求是从推送消息进入到Feed相应页面
        // mExtraData.putString("viewFrom", "push");
        // }

        String commentId = mExtraData.getString(HttpProtocol.COMMENT_ID_KEY);
        mExtraData.clear();
        if (!TextUtils.isEmpty(commentId)) {
            mExtraData.putString(HttpProtocol.COMMENT_ID_KEY, commentId);
        }
        // 表示该请求是从推送消息进入到Feed相应页面
        mExtraData.putString("viewFrom", "push");
    }

    public void fetchFeedWithId(final String feedId) {
        mActivityView.showLoading(true);
        // 获取feed的信息
        mCommunitySDK.fetchFeedWithId(feedId, mExtraData,
                new SimpleFetchListener<FeedItemResponse>() {

                    @Override
                    public void onComplete(FeedItemResponse response) {
                        mActivityView.showLoading(false);
                        if (NetworkUtils.handleResponseAll(response)) {
                            mActivityView.fetchFeedFaild();
                            return;
                        }
                        if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
                            deleteFeedFromDB(feedId);
                            FeedItem feedItem = new FeedItem();
                            feedItem.id = feedId;
                            BroadcastUtils.sendFeedDeleteBroadcast(mContext, feedItem);
//                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                            ((Activity) mContext).finish();
                            return;
                        }
                        DatabaseAPI.getInstance().getFeedDBAPI().saveFeedToDB(response.result);
                        if (response.result != null && response.errCode == ErrorCode.NO_ERROR) {
                            mFeedItem = response.result;
                        }
                        mActivityView.fetchDataComplete(response.result);

                    }
                });
    }

    /**
     * 从server端删除该条feed</br>
     */
    private void deleteFeed() {
        mCommunitySDK.deleteFeed(mFeedItem.id,
                new CommListener() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(Response response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            return;
                        }

                        if (response.errCode == 0) {
                            if (mActivityView != null) {
                                mActivityView.deleteFeedSuccess();
                            }
                            deleteFeedFromDB(mFeedItem.id);
                            BroadcastUtils.sendFeedDeleteBroadcast(mContext, mFeedItem);
                            sendDeleteFeedBroadcast();
                        }

                        final String resName = response.errCode == 0 ? "umeng_comm_delete_success" :
                                "umeng_comm_delete_failed";
                        ToastMsg.showShortMsgByResName(resName);
                    }
                });
    }

    /**
     * 发布删除feed的广播，更新其他UI界面的转发数量
     */
    private void sendDeleteFeedBroadcast() {
        BroadcastUtils.sendFeedDeleteBroadcast(mContext, mFeedItem);
        FeedItem originFeedItem = mFeedItem.sourceFeed;
        // 将原始的feed数量减1
        if (originFeedItem != null
                && !mFeedItem.sourceFeedId.equals(originFeedItem.id)) {
            mFeedItem.sourceFeed.forwardCount--;
            BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem.sourceFeed);
        }
    }

    /**
     * 从数据库中删除此feed</br>
     */
    private void deleteFeedFromDB(String feedId) {
        mDatabaseAPI.getFeedDBAPI().deleteFeedFromDB(feedId);
    }

    private void reportCurrentFeed() {
        SimpleFetchListener<LoginResponse> loginListener = new SimpleFetchListener<LoginResponse>() {
            @Override
            public void onComplete(LoginResponse response) {
                if (response.errCode != ErrorCode.NO_ERROR) {
                    return;
                }
                // 举报feed
                mCommunitySDK.spammerFeed(mFeedItem.id,
                        new FetchListener<SimpleResponse>() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(SimpleResponse response) {
                                if (NetworkUtils.handleResponseComm(response)) {
                                    return;
                                }
                                if (response.errCode == ErrorCode.NO_ERROR) {
                                    ToastMsg.showShortMsgByResName(
                                            "umeng_comm_discuss_spammer_success");
                                } else if (response.errCode == ErrorCode.REPORT_SAME_FEED) {
                                    ToastMsg.showShortMsgByResName(
                                            "umeng_comm_discuss_spammered");
                                } else {
                                    ToastMsg.showShortMsgByResName("umeng_comm_discuss_spammer_failed");
                                }
                            }
                        });
            }
        };
        CommonUtils.checkLoginAndFireCallback(mContext, loginListener);
    }
    private void reportFeedUser() {
        SimpleFetchListener<LoginResponse> loginListener = new SimpleFetchListener<LoginResponse>() {
            @Override
            public void onComplete(LoginResponse response) {
                if (response.errCode != ErrorCode.NO_ERROR) {
                    return;
                }
                // 举报feed
                mCommunitySDK.spamUser(mFeedItem.creator.id,
                        new FetchListener<SimpleResponse>() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(SimpleResponse response) {
                                if (NetworkUtils.handleResponseComm(response)) {
                                    return;
                                }
                                if (response.errCode == ErrorCode.NO_ERROR) {
                                    ToastMsg.showShortMsgByResName(
                                            "umeng_comm_discuss_spammer_success");
                                } else if (response.errCode == ErrorCode.REPORT_SAME_FEED) {
                                    ToastMsg.showShortMsgByResName(
                                            "umeng_comm_discuss_spammered");
                                } else {
                                    ToastMsg.showShortMsgByResName("umeng_comm_discuss_spammer_failed");
                                }
                            }
                        });
            }
        };
        CommonUtils.checkLoginAndFireCallback(mContext, loginListener);
    }
    public void favoritesFeed() {
        if(mFeedItem == null){
            return;
        }
        mCommunitySDK.favoriteFeed(mFeedItem.id, new SimpleFetchListener<SimpleResponse>() {

            @Override
            public void onComplete(SimpleResponse response) {
                if (response.errCode != ErrorCode.NO_ERROR) {
                    if (response.errCode == ErrorCode.ERR_CODE_FEED_FAVOURITED) {
                        mActivityView.favoriteFeedComplete(mFeedItem.id, true);
                        ToastMsg.showShortMsgByResName("umeng_comm_discuss_has_favorited");
                        mFeedItem.category = FeedItem.CATEGORY.FAVORITES;
                        mFeedItem.isCollected = true;
                        BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem);
                    } else if (response.errCode == ErrorCode.ERR_CODE_FAVOURITED_OVER_FLOW) {
                        ToastMsg.showShortMsgByResName("umeng_comm_favorites_overflow");
                    } else if (response.errCode == ErrorCode.USER_FORBIDDEN_ERR_CODE) {
                        ToastMsg.showShortMsgByResName("umeng_comm_user_unusable");
                    } else if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
                        ToastMsg.showShortMsgByResName("umeng_comm_discuss_favorite_failed_deleted");
                    } else {
                        ToastMsg.showShortMsgByResName("umeng_comm_discuss_favorite_failed");
                    }
                } else {
                    mActivityView.favoriteFeedComplete(mFeedItem.id, true);
                    ToastMsg.showShortMsgByResName("umeng_comm_favorites_success");
                    mFeedItem.category = FeedItem.CATEGORY.FAVORITES;
                    mFeedItem.isCollected = true;
                    mFeedItem.addTime = String.valueOf(System.currentTimeMillis());
                    DatabaseAPI.getInstance().getFeedDBAPI().saveFeedToDB(mFeedItem);
                    // 数据同步
                    BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem);
                    BroadcastUtils.sendFeedFavouritesBroadcast(mContext, mFeedItem);
                }
            }
        });
    }

    public void cancelFavoritesFeed() {
        if(mFeedItem == null){
            return;
        }
        mCommunitySDK.cancelFavoriteFeed(mFeedItem.id,
                new SimpleFetchListener<SimpleResponse>() {

                    @Override
                    public void onComplete(SimpleResponse response) {
                        if (response.errCode != ErrorCode.NO_ERROR) {
                            if (response.errCode == ErrorCode.ERR_CODE_FEED_NOT_FAVOURITED) {
                                mActivityView.favoriteFeedComplete(mFeedItem.id, false);
                                ToastMsg.showShortMsgByResName("umeng_comm_discuss_not_favorited");
                                mFeedItem.category = FeedItem.CATEGORY.NORMAL;
                                mFeedItem.isCollected = false;
                                BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem);
                            } else if (response.errCode == ErrorCode.USER_FORBIDDEN_ERR_CODE) {
                                ToastMsg.showShortMsgByResName("umeng_comm_user_unusable");
                            } else {
                                ToastMsg.showShortMsgByResName("umeng_comm_discuss_cancel_favorite_failed");
                            }
                        } else {
                            mActivityView.favoriteFeedComplete(mFeedItem.id, false);
                            ToastMsg.showShortMsgByResName("umeng_comm_cancel_favorites_success");
                            mFeedItem.category = FeedItem.CATEGORY.NORMAL;
                            mFeedItem.isCollected = false;
                            DatabaseAPI.getInstance().getFeedDBAPI().saveFeedToDB(mFeedItem);
                            // 数据同步
                            BroadcastUtils.sendFeedUpdateBroadcast(mContext, mFeedItem);
                        }
                    }
                });
    }

    public void shareToSns(Activity activity) {
        ShareContent shareItem = new ShareContent();
        shareItem.mText = mFeedItem.text;
        List<ImageItem> imageItems = mFeedItem.imageUrls;
        if (mFeedItem.sourceFeed != null) {
            imageItems = mFeedItem.sourceFeed.imageUrls;
        }
        if (imageItems.size() > 0) {
            shareItem.mImageItem = imageItems.get(0);
        }
        shareItem.mTargetUrl = mFeedItem.shareLink;
        if (TextUtils.isEmpty(shareItem.mTargetUrl) && mFeedItem.sourceFeed != null) {
            shareItem.mTargetUrl = mFeedItem.sourceFeed.shareLink;
        }
        shareItem.mFeedId = mFeedItem.id;
        if (mFeedItem.title.equals("null") || TextUtils.isEmpty(mFeedItem.title)){
            shareItem.mTitle = "默认标题";
        }else {
            shareItem.mTitle = mFeedItem.title;
        }
        ShareSDKManager.getInstance().getCurrentSDK().share(activity, shareItem);
    }

    public void forbidUser(final CommUser user, String topicId){
        mCommunitySDK.forbidUser(user.id, topicId, new SimpleFetchListener<ForbidResponse>() {
            @Override
            public void onComplete(ForbidResponse response) {
                String tipStr = "umeng_comm_forbid_user_failed";
                if(response != null && response.errCode == ErrorCode.NO_ERROR){
                    tipStr = "umeng_comm_forbid_user_success";
                    user.status = 4;
                    mActivityView.forbidUserComplete();
                    DatabaseAPI.getInstance().getUserDBAPI().saveUserInfoToDB(user);
                }
                else if (response != null && response.errCode == ErrorCode.ERROR_USER_CANNOT_SPAM){
                    tipStr = "umeng_comm_forbid_cannot";
                }
                else if (response != null && response.errCode == ErrorCode.ERR_CODE_USER_REPEAT_FORBID){
                    tipStr = "umeng_comm_forbid_user_failed_repeat";
                }

                ToastMsg.showShortMsgByResName(tipStr);
            }
        });
    }

    public void cancelForbidUser(final CommUser user, String topicId) {
//        mCommunitySDK.cancelForbidUser(user.id, topicId, new SimpleFetchListener<ForbidResponse>() {
//            @Override
//            public void onComplete(ForbidResponse response) {
//                String tipStr = "umeng_comm_unforbid_user_failed";
//                if(response != null && response.errCode == ErrorCode.NO_ERROR){
//                    tipStr = "umeng_comm_unforbid_user_success";
//                    user.status = 0;
//                    mActivityView.cancelForbidUserComplete();
//                    DatabaseAPI.getInstance().getUserDBAPI().saveUserInfoToDB(user);
//                }
//                else if (response != null && response.errCode == ErrorCode.ERROR_USER_CANNOT_UNSPAM){
//                    tipStr = "umeng_comm_unforbid_cannot";
//                }
//                ToastMsg.showShortMsgByResName(tipStr);
//            }
//        });
    }
}
