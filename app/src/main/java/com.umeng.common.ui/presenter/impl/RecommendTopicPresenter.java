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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class RecommendTopicPresenter extends BaseFragmentPresenter<List<Topic>> {

    protected MvpRecommendTopicView mRecommendTopicView;
    
    private String mNextPageUrl = "";

    private boolean isRegisterLoginReceiver;

    public RecommendTopicPresenter(MvpRecommendTopicView recommendTopicView) {
        this.mRecommendTopicView = recommendTopicView;
    }

    public RecommendTopicPresenter(MvpRecommendTopicView recommendTopicView, boolean isRegisterLoginReceiver) {
        this.mRecommendTopicView = recommendTopicView;
        this.isRegisterLoginReceiver = isRegisterLoginReceiver;
    }
    @Override
    public void attach(Context context) {
        super.attach(context);
        BroadcastUtils.registerTopicBroadcast(mContext, mReceiver);
        if(isRegisterLoginReceiver){
            registerLoginSuccessBroadcast();
        }
    }

    @Override
    public void detach() {
        BroadcastUtils.unRegisterBroadcast(mContext, mReceiver);
        if(isRegisterLoginReceiver){
            mContext.unregisterReceiver(mLoginReceiver);
        }
        super.detach();
    }

    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchRecommendedTopics(new FetchListener<TopicResponse>() {

            @Override
            public void onStart() {
                mRecommendTopicView.onRefreshStart();
            }

            @Override
            public void onComplete(final TopicResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    if(response.errCode == ErrorCode.NO_ERROR && CommonUtils.isListEmpty(response.result)){
                        mRecommendTopicView.getBindDataSource().clear();
                        mRecommendTopicView.notifyDataSetChanged();
                    }
                    mRecommendTopicView.onRefreshEnd(); // [注意]:不可移动，该方法的回调会决定是否显示空视图
                    return;
                }
                List<Topic> results = response.result;
//                updateNextPageUrl(response.nextPageUrl);
                dealNextPageUrl(response.nextPageUrl, true);
                fetchTopicComplete(results, true);
                mRecommendTopicView.onRefreshEnd();
                mRecommendTopicView.setIsVisitBtn(response.isVisit);
            }
        });
    }
    
//    protected void updateNextPageUrl(String newUrl) {
//        if (TextUtils.isEmpty(CommConfig.getConfig().loginedUser.id)) {
//            return;
//        }
//        List<Topic> dataSource = mRecommendTopicView.getBindDataSource();

//        for (Topic topic : dataSource) {
//            topic.nextPage = newUrl;
//        }
//        mRecommendTopicView.notifyDataSetChanged();
//    }
    
    protected void dealNextPageUrl(String url,boolean fromRefresh){
        if ( fromRefresh && TextUtils.isEmpty(mNextPageUrl) ) {
            mNextPageUrl = url;
        } else if (!fromRefresh) {
            mNextPageUrl = url;
        }
    }

    protected void fetchTopicComplete(List<Topic> topics, boolean fromRefersh) {
//        parseNextpageUrl(topics, fromRefersh);
        // 过滤已经存在的数据
        final List<Topic> newTopics = filterTopics(topics);
        if (newTopics != null && newTopics.size() > 0) {
            // 添加新话题
            List<Topic> dataSource = mRecommendTopicView.getBindDataSource();
            if (fromRefersh) {
                dataSource.addAll(0, newTopics);
            } else {
                dataSource.addAll(newTopics);// 加载更多的数据追加到尾部
            }
            mRecommendTopicView.notifyDataSetChanged();

            saveTopicToDB(topics, fromRefersh);
        }
    }

    protected void saveTopicToDB(List<Topic> topics, boolean isRefresh){
        if(isRefresh){
            mDatabaseAPI.getTopicDBAPI().deleteAllRecommendTopics();
        }
        // 将新的话题数据插入到数据库中
        saveDataToDB(topics);
    }

    @Override
    protected void saveDataToDB(List<Topic> topics) {
        // update topic cache
        DatabaseAPI.getInstance().getTopicDBAPI().saveRecommendTopicToDB(topics);

        List<Topic> tempList = new ArrayList();
        for (Topic topicItem : topics) {
            if (topicItem.isFocused) {
                tempList.add(topicItem);
            }
        }
        CommUser user = CommConfig.getConfig().loginedUser;
        DatabaseAPI.getInstance().getTopicDBAPI().saveFollowedTopicsToDB(user.id, tempList);
    }

    /**
     * 移除重复的话题</br>
     * 
     * @param dest 目标话题列表。
     * @return
     */
    private List<Topic> filterTopics(List<Topic> dest) {
        List<Topic> src = mRecommendTopicView.getBindDataSource();
        src.removeAll(dest);
        return dest;
    }

    @Override
    public void loadDataFromDB() {
//        mRecommendTopicView.onRefreshEndNoOP();
        mDatabaseAPI.getTopicDBAPI().loadRecommendTopicsFromDB(new SimpleFetchListener<List<Topic>>() {
            @Override
            public void onComplete(List<Topic> response) {
                if(mRecommendTopicView.getBindDataSource().isEmpty() && response != null){
                    mRecommendTopicView.getBindDataSource().addAll(response);
                    mRecommendTopicView.notifyDataSetChanged();
                    mRecommendTopicView.onRefreshEnd();
                }
            }
        });
    }

    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(mNextPageUrl))  {
            mRecommendTopicView.onRefreshEnd();
            return ;
        }
        mCommunitySDK.fetchNextPageData(mNextPageUrl,TopicResponse.class, new FetchListener<TopicResponse>() {

            @Override
            public void onStart() {
                
            }

            @Override
            public void onComplete(TopicResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                final List<Topic> results = response.result;
//                updateNextPageUrl(response.nextPageUrl);
                dealNextPageUrl(response.nextPageUrl, false);
                fetchTopicComplete(results, false);
                mRecommendTopicView.onRefreshEnd();
            }
        });
    }

    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveTopic(Intent intent) {
            Topic topic = getTopic(intent);
            if (topic != null) {
                Topic originTopic = findTopicById(topic.id);
                originTopic.isFocused = topic.isFocused;
                mRecommendTopicView.notifyDataSetChanged();
            }
        }
    };

    protected Topic findTopicById(String id) {
        List<Topic> dataSource = mRecommendTopicView.getBindDataSource();
        for (Topic topic : dataSource) {
            if (topic.id.equals(id)) {
                return topic;
            }
        }
        return new Topic();
    }

    /**
     * 关注某个话题</br>
     * 
     * @param topic 话题的id
     */
    public void followTopic(final Topic topic, final ToggleButton toggleButton) {
        mCommunitySDK.followTopic(topic, new SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                toggleButton.setClickable(true);
                if (NetworkUtils.handleResponseComm(response)) {
                    toggleButton.setChecked(false);
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_follow_failed");
                    return;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    topic.isFocused = true;
                    topic.fansCount += 1;
                    mRecommendTopicView.notifyDataSetChanged();
                    // 存储到数据
                    updateTopicFollowedState(topic);
                    BroadcastUtils.sendTopicFollowBroadcast(mContext, topic);
                } else if (response.errCode == ErrorCode.ORIGIN_TOPIC_DELETE_ERR_CODE) {
                    // 在数据库中删除该话题并Toast
                    deleteTopic(topic);
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_has_deleted");
                } else if (response.errCode == ErrorCode.ERROR_TOPIC_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_has_focused");
                    toggleButton.setChecked(true);
                } else {
                    toggleButton.setChecked(false);
                    ToastMsg.showShortMsgByResName("umeng_comm_topic_follow_failed");
                }
                toggleButton.setClickable(true);
            }
        });
    }

    /**
     * 检测是否登录并执行关注/取消关注操作</br>
     * 
     * @param topic
     * @param toggleButton
     * @param isFollow
     */
    public void checkLoginAndExecuteOp(final Topic topic, final ToggleButton toggleButton,
            final boolean isFollow) {


            CommonUtils.checkLoginAndFireCallback(mContext, new SimpleFetchListener<LoginResponse>() {

                @Override
                public void onComplete(LoginResponse response) {

                    if (response.errCode != ErrorCode.NO_ERROR) {
                        toggleButton.setChecked(!toggleButton.isChecked());

                        return;
                    }
                    if (isFollow) {
                        followTopic(topic, toggleButton);
                    } else {
                        cancelFollowTopic(topic, toggleButton);
                    }
                }
            });

    }

    /**
     * 取消关注某个话题</br>
     * 
     * @param topic
     */
    public void cancelFollowTopic(final Topic topic, final ToggleButton toggleButton) {
        mCommunitySDK.cancelFollowTopic(topic,
                new SimpleFetchListener<Response>() {

                    @Override
                    public void onComplete(Response response) {
                        toggleButton.setClickable(true);
                        if ( NetworkUtils.handleResponseComm(response) ) {
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_cancel_failed");
                            toggleButton.setChecked(true);
                            return ;
                        }
                        if (response.errCode == ErrorCode.ORIGIN_TOPIC_DELETE_ERR_CODE) {
                            // 在数据库中删除该话题并Toast
                            deleteTopic(topic);
                            ToastMsg.showShortMsgByResName("umeng_comm__topic_has_deleted");
                            return;
                        }

                        if (response.errCode == ErrorCode.NO_ERROR) {
                            topic.isFocused = false;
                            topic.fansCount -= 1;
                            mRecommendTopicView.notifyDataSetChanged();
                            // 将该记录从数据库中移除
                            DatabaseAPI.getInstance().getTopicDBAPI().deleteFollowedTopicByTopicId(topic.id);
                            BroadcastUtils.sendTopicCancelFollowBroadcast(mContext, topic);
                            updateTopicFollowedState(topic);
                        } else if (response.errCode == ErrorCode.ERROR_TOPIC_NOT_FOCUSED) {
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_has_not_focused");
                            toggleButton.setChecked(false);
                        } else {
                            toggleButton.setChecked(true);
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_cancel_failed");
                        }
                    }
                });
    }

    private void updateTopicFollowedState(Topic topic){
        String currentUid = CommConfig.getConfig().loginedUser.id;
        if(topic.isFocused){
            mDatabaseAPI.getTopicDBAPI().saveFollowedTopicToDB(currentUid, topic);
        }else{
            mDatabaseAPI.getTopicDBAPI().deleteFollowedTopicByUid(currentUid);
        }
    }

    /**
     * 删除话题。包括删除关系表跟话题本身，以及从adapter中删除</br>
     * 
     * @param topic
     */
    private void deleteTopic(Topic topic) {
        DatabaseAPI.getInstance().getTopicDBAPI().deleteTopicFromDB(topic.id);
        // 从adapter删除该条topic
        mRecommendTopicView.getBindDataSource().remove(topic);
        mRecommendTopicView.notifyDataSetChanged();
    }

    /**
     * 根据关键字从本地搜索话题</br>
     * 
     * @param keyword 话题关键字
     * @return 能够匹配keyword的话题列表
     */
    public List<Topic> localSearchTopic(String keyword) {
        List<Topic> resultList = new ArrayList<Topic>();
        final List<Topic> topics = mRecommendTopicView.getBindDataSource();
        String name = null;
        for (Topic topic : topics) {
            name = topic.name;
            if (!TextUtils.isEmpty(name) && name.contains(keyword)) {
                resultList.add(topic);
            }
        }
        return resultList;
    }

    /**
     * 注册登录成功时的广播</br>
     */
    private void registerLoginSuccessBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_LOGIN_SUCCESS);
        mContext.registerReceiver(mLoginReceiver, filter);
    }

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mRecommendTopicView.onUserLogin();
        }
    };
}
