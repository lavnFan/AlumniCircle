package com.umeng.common.ui.presenter.impl;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;

import java.util.List;

/**
 * Created by wangfei on 15/12/1.
 */
public class RecommendTopicFgPresenter extends TopicFgPresenter {
    public RecommendTopicFgPresenter(MvpRecommendTopicView recommendTopicView) {
        super(recommendTopicView);
    }

    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchRecommendedTopics(new Listeners.FetchListener<TopicResponse>() {

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

//                clearTopicCacheAfterFirstRefresh();
                mDatabaseAPI.getTopicDBAPI().deleteAllTopics();
                final List<Topic> results = response.result;
                dealNextPageUrl(response.nextPageUrl, true);
                fetchTopicComplete(results, true);
                mRecommendTopicView.onRefreshEnd();
            }
        });
    }
}
