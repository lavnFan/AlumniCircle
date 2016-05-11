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

import com.umeng.comm.core.beans.MessageSession;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.MessageSessionResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.common.ui.mvpview.MvpMessageSessionView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;

import java.util.List;

public class MessageSessionPresenter extends BaseFragmentPresenter<List<MessageSession>> {

    private MvpMessageSessionView mvpMessageSessionView;
    private String nextPage;

    public MessageSessionPresenter(MvpMessageSessionView view) {
        mvpMessageSessionView = view;
    }

//    private List<MessageSession> removeExsitItems(List<MessageSession> newItems) {
//        newItems.removeAll(mvpMessageSessionView.getBindDataSource());
//        return newItems;
//    }

    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchSessionList(new SimpleFetchListener<MessageSessionResponse>() {
            @Override
            public void onComplete(MessageSessionResponse response) {
                deliveryResponse(response, false);
            }
        });
    }

    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(nextPage)) {
            mvpMessageSessionView.onRefreshEnd();
            return;
        }
        mCommunitySDK.fetchNextPageData(nextPage,MessageSessionResponse.class,
                new SimpleFetchListener<MessageSessionResponse>() {
            @Override
            public void onComplete(MessageSessionResponse response) {
                deliveryResponse(response, true);
            }
        });
    }

    private void deliveryResponse(MessageSessionResponse response, boolean append) {
        if (NetworkUtils.handleResponseAll(response)) {
            mvpMessageSessionView.onRefreshEnd();
            return;
        }
        if (append) {
            mvpMessageSessionView.getBindDataSource().removeAll(response.result);
            mvpMessageSessionView.getBindDataSource().addAll(response.result);
            nextPage = response.nextPageUrl;
        } else {
            mvpMessageSessionView.getBindDataSource().clear();
            mvpMessageSessionView.getBindDataSource().addAll(response.result);
            if (TextUtils.isEmpty(nextPage)) {
                nextPage = response.nextPageUrl;
            }
        }
        mvpMessageSessionView.notifyDataSetChange();
        mvpMessageSessionView.onRefreshEnd();
    }
}
