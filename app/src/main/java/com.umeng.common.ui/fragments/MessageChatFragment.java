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

package com.umeng.common.ui.fragments;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.umeng.comm.core.beans.MessageChat;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.MessageChatAdapter;
import com.umeng.common.ui.mvpview.MvpMessageChatView;
import com.umeng.common.ui.presenter.impl.MessageChatPresenter;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;

/**
 * 消息通知Fragment
 */
public class MessageChatFragment extends BaseFragment<List<MessageChat>, MessageChatPresenter>
        implements MvpMessageChatView {

    private ListView mListView;
    private RefreshLvLayout mRefreshLayout;

    private MessageChatAdapter mAdapter;

    private String mUid;

    private EditText mEditText;

    public static MessageChatFragment newMessageChatFragment(String uId) {
        MessageChatFragment fragment = new MessageChatFragment();
        fragment.mUid = uId;
        return fragment;
    }


    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_message_chat_fragment");
    }

    @Override
    protected MessageChatPresenter createPresenters() {
        return new MessageChatPresenter(this, mUid);
    }

    @Override
    protected void initWidgets() {
        mRefreshLayout = findViewById(ResFinder.getId("umeng_comm_swipe_layout"));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//                mPresenter.loadMoreData();
                mPresenter.loadDataFromServer();
            }
        });
//        mRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
//            @Override
//            public void onLoad() {
//                mPresenter.loadDataFromServer();
//            }
//        });
        // 添加footer
        mRefreshLayout.setDefaultFooterView();
        mListView = findViewById(ResFinder.getId("umeng_comm_notify_listview"));
        mAdapter = new MessageChatAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setDividerHeight(0);
        mEditText = findViewById(ResFinder.getId("umeng_comm_message_chat_edittext"));
        findViewById(ResFinder.getId("umeng_comm_message_chat_send_btn")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditText == null || mEditText.getText() == null || TextUtils.isEmpty(mEditText.getText().toString())) {
                    return;
                }
                mPresenter.sendChatMessage(mEditText.getText().toString());
            }
        });
    }

    @Override
    public void onRefreshEnd() {
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setLoading(false);
    }

    @Override
    public List<MessageChat> getBindDataSource() {
        return mAdapter.getDataSource();
    }

    @Override
    public void notifyDataSetChange() {
        mAdapter.notifyDataSetChanged();
//        int total = CommConfig.getConfig().mMessageCount.unReadTotal;
//        int unReadNotice = CommConfig.getConfig().mMessageCount.unReadNotice;
//        CommConfig.getConfig().mMessageCount.unReadTotal = total - unReadNotice;
//        CommConfig.getConfig().mMessageCount.unReadNotice = 0;
    }

    @Override
    public void onRefreshStart() {
    }

    @Override
    public void sendChatMessageSuccess() {
        mEditText.setText("");
        mEditText.getHandler().post(new Runnable() {
            @Override
            public void run() {
                scrollToBottom();
            }
        });
    }

    @Override
    public void scrollToBottom() {
        mListView.setSelection(ListView.FOCUS_DOWN);
    }

    @Override
    public void scrollToPositon(int position) {
        mListView.setSelectionFromTop(position, 50);
    }
}
