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
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.UsersResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpSearchFgView;

import java.util.List;


/**
 * 
 */
public class SearchPresenter extends FeedListPresenter {

    private String mUserNextPage = "";
    private MvpSearchFgView mSearchView;
    private boolean executeLoading = false;

    /**
     * @param searchFgView
     * @param searchFgView
     */
    public SearchPresenter(MvpSearchFgView searchFgView) {
        super(searchFgView);
        this.mSearchView = searchFgView;
    }

    /**
     * 加载更多的关注用户</br>
     */
    public void loadMoreUser() {
        if (TextUtils.isEmpty(mUserNextPage) || executeLoading) {
            ToastMsg.showShortMsgByResName("umeng_comm_text_load_over");
            mSearchView.onRefreshEnd();
            return;
        }
        executeLoading = true;
        mCommunitySDK.fetchNextPageData(mNextPageUrl, UsersResponse.class,
                new SimpleFetchListener<UsersResponse>() {

                    @Override
                    public void onComplete(UsersResponse response) {
                        mSearchView.onRefreshEnd();
                        mUserNextPage = response.nextPageUrl;
                        if (NetworkUtils.handleResponseAll(response)) {
                            return;
                        }
                        mSearchView.getUserDataSource().addAll(response.result);
                        mSearchView.notifyDataSetChanged();
                        executeLoading = false;
                    }
                });
    }

    public void executeSearch(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            ToastMsg.showShortMsgByResName("umeng_comm_topic_search_no_keyword");
            return;
        }
        mSearchView.hideInputMethod();
        // 搜索Feed
        mCommunitySDK.searchFeed(keyword,
                new SimpleFetchListener<FeedsResponse>() {

                    @Override
                    public void onComplete(FeedsResponse response) {
                        if (response.errCode == ErrorCode.UNLOGIN_ERROR){
                            NetworkUtils.UnLogin((Activity) mContext, new LoginListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int stCode, CommUser userInfo) {
//                                    executeSearch(keyword);
                                }
                            });
                            return;
                        }
                        List<FeedItem> feedItems = response.result;
                        mSearchView.getBindDataSource().clear();
                        if (feedItems.size() == 0) {
                            Log.e("xxxx","feed show");
                            mSearchView.showFeedEmptyView();
                            mSearchView.notifyDataSetChanged();
                        } else {
                            Log.e("xxxx","feed hide="+feedItems.size());
                            mSearchView.getBindDataSource().addAll(feedItems);
                            mSearchView.notifyDataSetChanged();
                            mSearchView.hideFeedEmptyView();
                        }
                        mNextPageUrl = response.nextPageUrl;
                    }
                });

        // 搜索用户
        mCommunitySDK.searchUser(keyword,
                new SimpleFetchListener<UsersResponse>() {

                    @Override
                    public void onComplete(UsersResponse response) {
//                        if (response.errCode == ErrorCode.UNLOGIN_ERROR){
//                            NetworkUtils.UnLogin((Activity) mContext, new LoginListener() {
//                                @Override
//                                public void onStart() {
//
//                                }
//
//                                @Override
//                                public void onComplete(int stCode, CommUser userInfo) {
//
//                                }
//                            });
//                            return;
//                        }
                        if (response.errCode == ErrorCode.UNLOGIN_ERROR){

                            return;
                        }
                        List<CommUser> users = response.result;
                        mSearchView.getUserDataSource().clear();
                        if (users.size() == 0) {
                            mSearchView.showUserEmptyView();
                        } else {
                            mSearchView.hideUserEmptyView();
                        }
                        mSearchView.showRelativeUserView(users);
                        mUserNextPage = response.nextPageUrl;
                    }
                });
    }

    public String getUserNextUrl() {
        return mUserNextPage;
    }

    @Override
    public void loadDataFromDB() {
    }

    @Override
    public void loadDataFromServer() {
    }

}
