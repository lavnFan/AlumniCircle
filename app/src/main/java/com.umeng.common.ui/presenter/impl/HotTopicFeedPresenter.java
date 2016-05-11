package com.umeng.common.ui.presenter.impl;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.common.ui.mvpview.MvpFeedView;

import java.util.Comparator;


/**
 * Created by wangfei on 15/12/2.
 */
public class HotTopicFeedPresenter extends TopicFeedPresenter {
    private int hottype = 1;

    public HotTopicFeedPresenter(MvpFeedView view) {
        super(view);
    }
    public void loadDataFromServer(int days){
        if (hottype!=days){
            mFeedView.getBindDataSource().clear();
            mFeedView.notifyDataSetChanged();
            hottype = days;
        }
        hottype = days;
        loadDataFromServer();
    }
    @Override
    public void loadDataFromServer() {
        int days = 1;

        switch (hottype){
            case 0:
                days =1;
                break;
            case 1:
                days =3;
                break;
            case 2:
                days =7;
                break;
            case 3:
                days =30;
                break;
        }
        mCommunitySDK.fetchTopicHotestFeeds(mId, mRefreshListener, days, 0);
    }

    @Override
    protected Comparator<FeedItem> getFeedCompartator() {
        return null;
    }

    @Override
    public void loadDataFromDB() {

    }
}
