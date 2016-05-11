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

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.List;


/**
 * 用户关注的话题Presenter
 * 
 * @author mrsimple
 */
public class FollowedTopicPresenter extends TopicFgPresenter {

    String mNextPage;

    String mUid;

    public FollowedTopicPresenter(String uid, MvpRecommendTopicView recommendTopicView) {
        super(recommendTopicView);
        mUid = uid;
    }
    @Override
    public void attach(Context context) {
        super.attach(context);
        BroadcastUtils.registerTopicBroadcast(mContext, mReceiver);
    }
    @Override
    public void loadDataFromDB() {
//        mRecommendTopicView.onRefreshEndNoOP();
        mDatabaseAPI.getTopicDBAPI().loadTopicsFromDB(mUid, mDbFetchListener);
    }
    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveTopic(Intent intent) {
            Topic topic = getTopic(intent);
            if (topic != null) {
                Topic originTopic = findTopicById(topic.id);

                originTopic.isFocused = topic.isFocused;
                mRecommendTopicView.getBindDataSource().remove(originTopic);
                mRecommendTopicView.notifyDataSetChanged();
            }
        }
    };
    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchFollowedTopics(mUid,
                new SimpleFetchListener<TopicResponse>() {

                    @Override
                    public void onComplete(final TopicResponse response) {
                        if (NetworkUtils.handleResponseAll(response)) {
                            mRecommendTopicView.onRefreshEnd();
                            return;
                        }

                        List<Topic> results = response.result;
                        List<Topic> mDatas = mRecommendTopicView.getBindDataSource();
                        // 去除重复的
                        mDatas.removeAll(results);
                        mDatas.addAll(0, results);
                        userTopicPolicy(mUid, results);
                        dealNextPageUrl(response.nextPageUrl, true);
                        mRecommendTopicView.notifyDataSetChanged();
                        mRecommendTopicView.onRefreshEnd();

                    }
                });
    }

    @Override
    protected void saveTopicToDB(List<Topic> topics, boolean isRefresh) {
        if(isRefresh){
            // 保存话题本身的数据后再保存用户关注的话题到数据库
            DatabaseAPI.getInstance().getTopicDBAPI().deleteFollowedTopicByUid(mUid);
            DatabaseAPI.getInstance().getTopicDBAPI().saveFollowedTopicsToDB(mUid, topics);
        }
    }

    /**
     * 对用户关注的话题设置已关注flag。在获取用户关注的话题时，server不返回is_focused。
     * 这里仅仅只针用户为当前登录用户时，才改变该值</br>
     * 
     * @param topics 从server端获取到的最新feed
     */
    private void userTopicPolicy(String uid, List<Topic> topics) {
        CommUser loginUser = CommConfig.getConfig().loginedUser;
        if (loginUser.id.equals(uid)) {
            for (Topic topic : topics) {
                topic.isFocused = true;
            }
        }
    }
}
