package com.umeng.common.ui.presenter.impl;


import com.umeng.comm.core.beans.FeedItem;
import com.umeng.common.ui.mvpview.MvpFeedView;

import java.util.Comparator;

/**
 * Created by wangfei on 15/12/2.
 */
public class RecommendTopicFeedPresenter extends TopicFeedPresenter{
    public RecommendTopicFeedPresenter(MvpFeedView view) {
        super(view);
    }
    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchTopicRecommendFeed(mId, mRefreshListener);
    }

    @Override
    public void loadDataFromDB() {

    }

    @Override
    protected Comparator<FeedItem> getFeedCompartator() {
        return null;
    }
}
