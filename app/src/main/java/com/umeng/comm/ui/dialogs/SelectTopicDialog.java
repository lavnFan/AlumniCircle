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

package com.umeng.comm.ui.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.SelectTopicAdapter;
import com.umeng.common.ui.dialogs.PickerDialog;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 发布Feed时选择话题的Dialog
 */
public class SelectTopicDialog extends PickerDialog<Topic> {
    /**
     * 话题下一页url地址。每次从server获取列表时，都能够拿到该url，因此不cache到DB
     */
    private String mNextPageUrl;

    private volatile AtomicBoolean mUpdateNextPageUrl = new AtomicBoolean(true);

    public SelectTopicDialog(Context context) {
        this(context, 0);
    }

    public SelectTopicDialog(Context context, int theme) {
        super(context, theme);
        setContentView(this.createContentView());
//        loadFriendsFromDB(mUser.id);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadDataFromServer();
    }

    @Override
    protected void setupAdater() {
        mAdapter = new SelectTopicAdapter(getContext());
        mRefreshLvLayout.setAdapter(mAdapter);
        String title = ResFinder.getString("umeng_comm_topic");
        mTitleTextView.setText(title);
        mListView.setFooterDividersEnabled(true);
        mListView.setOverscrollFooter(null);
    }

    @Override
    protected void setupLvOnItemClickListener() {
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickItemAtPosition(position);
            }
        });
    }

//    /**
//     * 从数据库中加载最近的联系人。</br>
//     *
//     * @param uid
//     */
//    private void loadFriendsFromDB(final String uid) {
//        DatabaseAPI.getInstance().getFollowDBAPI()
//                .loadFollowedUsersFromDB(uid, new SimpleFetchListener<List<CommUser>>() {
//
//                    @Override
//                    public void onComplete(List<CommUser> result) {
//                        mAdapter.addData(result);
//                    }
//                });
//    }

    @Override
    public void loadDataFromServer() {
        mSdkImpl.fetchTopics(new FetchListener<TopicResponse>() {

            @Override
            public void onStart() {
                mRefreshLvLayout.setRefreshing(true);
            }

            @Override
            public void onComplete(TopicResponse response) {
                mRefreshLvLayout.setRefreshing(false);
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                if (mUpdateNextPageUrl.get()) {
                    mNextPageUrl = response.nextPageUrl;
                    mUpdateNextPageUrl.set(false);
                }
                handleResultData(response);
            }
        });
    }

    @Override
    public void loadMore() {
        if (TextUtils.isEmpty(mNextPageUrl)) {
            mRefreshLvLayout.setLoading(false);
            return;
        }
        mSdkImpl.fetchNextPageData(mNextPageUrl, TopicResponse.class,
                new FetchListener<TopicResponse>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(TopicResponse response) {
                        mRefreshLvLayout.setLoading(false);
                        if (NetworkUtils.handleResponseAll(response)) {
                            return;
                        }
                        mNextPageUrl = response.nextPageUrl;
                        handleResultData(response);
                    }
                });
    }

    @Override
    protected void pickItemAtPosition(int position) {
        super.pickItemAtPosition(position);
        mSelectedItem = null;
    }

    /**
     * 处理从server加载后返回的数据</br>
     *
     * @param response
     */
    private void handleResultData(TopicResponse response) {
        List<Topic> users = response.result;
        List<Topic> sourceList = mAdapter.getDataSource();
        users.removeAll(sourceList);
        mAdapter.addData(users);
    }

}
