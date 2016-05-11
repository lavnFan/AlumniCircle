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

import com.umeng.comm.core.beans.MessageChat;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.MessageChatListResponse;
import com.umeng.comm.core.nets.responses.MessageChatResponse;
import com.umeng.comm.core.nets.responses.MessageSessionResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpMessageChatView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;

import java.util.List;

public class MessageChatPresenter extends BaseFragmentPresenter<List<MessageChat>> {

    private MvpMessageChatView mvpMessageChatView;
    private String nextPage;
    private String mUid;
    private boolean mIsInProgress;

    public MessageChatPresenter(MvpMessageChatView view, String uId) {
        mvpMessageChatView = view;
        this.mUid = uId;
    }

    private List<MessageChat> removeExsitItems(List<MessageChat> newItems) {
        newItems.removeAll(mvpMessageChatView.getBindDataSource());
        return newItems;
    }

    @Override
    public void loadDataFromServer() {
        if(mvpMessageChatView.getBindDataSource().size() > 0){
            loadMoreData();
        }else{
            mCommunitySDK.fetchChatList(mUid, new SimpleFetchListener<MessageChatListResponse>() {
                @Override
                public void onComplete(MessageChatListResponse response) {
                    if (response != null && response.result != null &&
                            response.errCode == ErrorCode.NO_ERROR) {
                        deliveryResponse(response, false);
                    }
                }
            });
        }
    }

    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(nextPage)) {
            mvpMessageChatView.onRefreshEnd();
            return;
        }
        mCommunitySDK.fetchNextPageData(nextPage, MessageChatListResponse.class,
                new SimpleFetchListener<MessageChatListResponse>() {
                    @Override
                    public void onComplete(MessageChatListResponse response) {
                        if (response != null && response.result != null &&
                                response.errCode == ErrorCode.NO_ERROR) {
                            deliveryResponse(response, true);
                        }
                    }
                });
    }

    public void sendChatMessage(String message){
        if(mIsInProgress){
            return;
        }
        mIsInProgress  = true;
        mCommunitySDK.sendChatMessage(mUid, message, new SimpleFetchListener<MessageChatResponse>() {
            @Override
            public void onComplete(MessageChatResponse response) {
                if(response != null){
                    if(NetworkUtils.handleResponseComm(response)){
                        return;
                    }
                    switch (response.errCode){
                        case ErrorCode.NO_ERROR:
                            if(response.result != null){
                                mvpMessageChatView.getBindDataSource().add(mvpMessageChatView.
                                        getBindDataSource().size(), response.result);
                                mvpMessageChatView.notifyDataSetChange();
                                mvpMessageChatView.sendChatMessageSuccess();
                                ToastMsg.showShortMsgByResName("umeng_comm_discuss_chat_success");
                            }
                            break;
                        case ErrorCode.ERR_CODE_USER_FORBIDDEN:
                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_chat_failed_forbid");
                            break;

                        default:
                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_chat_failed");
                            break;
                    }

                }
                mIsInProgress = false;
            }
        });
    }

    public void createChat(){
        mCommunitySDK.createSession(mUid, new SimpleFetchListener<MessageSessionResponse>() {
            @Override
            public void onComplete(MessageSessionResponse response) {

            }
        });
    }

    private void deliveryResponse(MessageChatListResponse response, boolean append) {
        mvpMessageChatView.onRefreshEnd();
        if (NetworkUtils.handleResponseAll(response)) {
            return;
        }
        nextPage = response.nextPageUrl;
        if (append) {
            mvpMessageChatView.getBindDataSource().addAll(0, removeExsitItems(response.result));
            mvpMessageChatView.notifyDataSetChange();
            mvpMessageChatView.scrollToPositon(response.result.size());
        } else {
            mvpMessageChatView.getBindDataSource().addAll(mvpMessageChatView.
                    getBindDataSource().size(), removeExsitItems(response.result));
            mvpMessageChatView.notifyDataSetChange();
            mvpMessageChatView.scrollToBottom();
        }

    }
}
