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
import android.content.Intent;
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.community.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 */
public class TopicFgPresenter extends RecommendTopicPresenter {

    private SearchTask mSearchTask = new SearchTask();
    private String mNextPageUrl = "";

    public TopicFgPresenter(MvpRecommendTopicView recommendTopicView) {
        super(recommendTopicView);
    }

    @Override
    public void attach(Context context) {
        super.attach(context);
        BroadcastUtils.registerTopicBroadcast(context, mReceiver);
    }

    @Override
    public void loadDataFromServer() {
        if (CommonUtils.getLoginUser(mContext) != null) {
            mCommunitySDK.fetchTopics(mRefreshListener);
        } else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }
    }

    private FetchListener<TopicResponse> mRefreshListener = new FetchListener<TopicResponse>() {

        @Override
        public void onStart() {
            mRecommendTopicView.onRefreshStart();
        }

        @Override
        public void onComplete(final TopicResponse response) {
            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response)) {
                //  如果是网络错误，其结果可能快于DB查询
                if (CommonUtils.isNetworkErr(response.errCode)) {
                    mRecommendTopicView.onRefreshEndNoOP();
                } else {
                    mRecommendTopicView.onRefreshEnd();
                }
                return;
            }
            // TODO 是否需要清空全部缓存
            mDatabaseAPI.getTopicDBAPI().deleteAllTopics();
            final List<Topic> results = response.result;
            dealNextPageUrl(response.nextPageUrl, true);
            fetchTopicComplete(results, true);
            mRecommendTopicView.onRefreshEnd();
        }
    };

    @Override
    public void loadDataFromDB() {
        mDatabaseAPI.getTopicDBAPI().loadTopicsFromDB(mDbFetchListener);
    }

    protected SimpleFetchListener<List<Topic>> mDbFetchListener = new SimpleFetchListener<List<Topic>>() {

        @Override
        public void onComplete(List<Topic> result) {
            if (!isActivityAlive()) {
                return;
            }
            // 更新listview数据
            mRecommendTopicView.getBindDataSource().addAll(0, result);
            mRecommendTopicView.notifyDataSetChanged();
            mRecommendTopicView.onRefreshEnd();
        }
    };

    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(mNextPageUrl)) {
            mRecommendTopicView.onRefreshEnd();
            return;
        }
        Log.d("TopicDialog", "加载更多下一页 : " + mNextPageUrl);
        mCommunitySDK.fetchNextPageData(mNextPageUrl, TopicResponse.class, mLoadMoreListener);
    }

    private SimpleFetchListener<TopicResponse> mLoadMoreListener = new SimpleFetchListener<TopicResponse>() {

        @Override
        public void onComplete(TopicResponse response) {
            mRecommendTopicView.onRefreshEnd();
            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response)) {
                return;
            }
            dealNextPageUrl(response.nextPageUrl, false);
            fetchTopicComplete(response.result, false);
        }
    };

    @Override
    protected void saveDataToDB(List<Topic> topics) {
        DatabaseAPI.getInstance().getTopicDBAPI().saveTopicsToDB(topics);
        List<Topic> tempList = new ArrayList();
        for (Topic topicItem : topics) {
            if (topicItem.isFocused) {
                tempList.add(topicItem);
            }
        }
        CommUser user = CommConfig.getConfig().loginedUser;
        DatabaseAPI.getInstance().getTopicDBAPI().saveFollowedTopicsToDB(user.id, tempList);
    }

    @Override
    protected void dealNextPageUrl(String url, boolean fromRefresh) {
        if (fromRefresh && TextUtils.isEmpty(mNextPageUrl)) {
            mNextPageUrl = url;
        } else if (!fromRefresh) {
            mNextPageUrl = url;
        }
    }

    /**
     * 话题搜索处理类
     */
    class SearchTask {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Void> future = null;

        /**
         * 请求根据关键字搜索话题。对于对个请求，总是以最新的请求为准</br>
         *
         * @param keyword 话题的关键字
         */
        public void execute(final String keyword) {
            if (TextUtils.isEmpty(keyword)) {
                ToastMsg.showShortMsgByResName("umeng_comm_search_keyword_input");
                return;
            }
            // 如果本次搜索未完成，直接取消，搜索新的话题
            cancelTask();
            // 构建Callable任务
            Callable<Void> callable = new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    // 异步搜索
                    mCommunitySDK.searchTopic(keyword,
                            new SimpleFetchListener<TopicResponse>() {

                                @Override
                                public void onComplete(TopicResponse response) {
                                    mRecommendTopicView.onRefreshEnd();
                                    updateResult(response.result);
                                }
                            });
                    return null;
                }
            };
            future = executorService.submit(callable);
        }

        // 取消未完成的搜索任务
        void cancelTask() {
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        }
    } // end of Task

    // 获取搜索结果并更新listView
    void updateResult(List<Topic> topics) {
        try {
            if (topics != null && topics.size() > 0) {
                updateTopicFocusable(topics);
                List<Topic> dataSource = mRecommendTopicView.getBindDataSource();
                dataSource.clear();
                dataSource.addAll(topics);
                mRecommendTopicView.notifyDataSetChanged();
            } else if (topics.size() == 0) {
                ToastMsg.showShortMsgByResName("umeng_comm_search_topic_failed");
            } else {
                ToastMsg.showShortMsgByResName("umeng_comm_search_topic_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新搜索到的话题的isFocus字段。由于Server比较难处理是否关注（数据量大），
     * 获取话题时不返回是否关注字段。目前暂时跟本地话题对比的方式</br>
     *
     * @param newTopics
     */
    private void updateTopicFocusable(List<Topic> newTopics) {
        List<Topic> dataSource = mRecommendTopicView.getBindDataSource();
        if (dataSource.size() == 0) {
            return;
        }

        int len = dataSource.size();
        Topic topic;
        for (int i = 0; i < len; i++) {
            topic = dataSource.get(i);
            if (newTopics.contains(topic)) {
                int index = newTopics.indexOf(topic);
                newTopics.get(index).isFocused = topic.isFocused;
            }
        }
    }

    /**
     * 搜索话题</br>
     *
     * @param keyword
     */
    public void executeSearch(String keyword) {
        mSearchTask.execute(keyword);
    }

    @Override
    public void detach() {
        mSearchTask.cancelTask();
        super.detach();
    }
}
