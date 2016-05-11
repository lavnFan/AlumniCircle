package com.umeng.common.ui.presenter.impl;

import android.app.Activity;
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.common.ui.mvpview.MvpRecyclerView;
import com.umeng.common.ui.mvpview.MvpSearchTopicFgView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;

import java.util.List;

/**
 * Created by umeng on 12/10/15.
 */
public class TopicSearchPresenter extends BaseFragmentPresenter<List<Topic>> {
    private String mNextPageUrl;
    private MvpRecyclerView mvpRecommendTopicView;
    private MvpSearchTopicFgView mvpSearchTopicFgView;
    private List<Topic> mTopicList;


    public TopicSearchPresenter(MvpRecyclerView mvpRecommendTopicView,List<Topic> topicList,MvpSearchTopicFgView mvpSearchTopicFgView) {
        this.mvpRecommendTopicView = mvpRecommendTopicView;
        this.mvpSearchTopicFgView = mvpSearchTopicFgView;
        this.mTopicList = topicList;
    }

    @Override
    public void loadDataFromServer() {

    }

    public void executeSearch(String searchKeyWord){
        mCommunitySDK.searchTopic(searchKeyWord, new Listeners.SimpleFetchListener<TopicResponse>() {
            @Override
            public void onComplete(TopicResponse response) {
                mvpRecommendTopicView.onRefreshEnd();
                if (response.errCode == ErrorCode.UNLOGIN_ERROR){
                    NetworkUtils.UnLogin((Activity) mContext, new LoginListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onComplete(int stCode, CommUser userInfo) {

                        }
                    });
                    return;
                }
                if(NetworkUtils.handleResponseAll(response)){
                    if (response.result.size() == 0){
                        mvpSearchTopicFgView.showFeedEmptyView();
                    }
                    else {
                        mvpSearchTopicFgView.hideFeedEmptyView();
                        mTopicList.clear();
                        mvpRecommendTopicView.onDataSetChanged();
                    }
                    return;
                }
                if (response.result.size() != 0){
                    mvpSearchTopicFgView.hideFeedEmptyView();
                }
                mNextPageUrl = response.nextPageUrl;
                mTopicList.clear();
                mTopicList.addAll(response.result);
                mvpRecommendTopicView.onDataSetChanged();
            }

            @Override
            public void onStart() {
                super.onStart();
                mvpRecommendTopicView.onRefreshStart();
            }
        });
    }

    public void fetchNextPageData(){

        if(!TextUtils.isEmpty(mNextPageUrl)){

            mCommunitySDK.fetchNextPageData(mNextPageUrl, TopicResponse.class, new Listeners.FetchListener<TopicResponse>() {
            @Override
            public void onStart() {
//                mvpRecommendTopicView.showProgressFooter();
//                mvpRecommendTopicView.onRefreshStart();
            }

            @Override
            public void onComplete(TopicResponse response) {
//                mvpRecommendTopicView.hideProgressFooter();
//                mvpRecommendTopicView.onRefreshEnd();
                if(NetworkUtils.handleResponseAll(response)){

                    return;
                }
                if(response!=null){
                    mNextPageUrl = response.nextPageUrl;

                    mTopicList.addAll(mTopicList.size()-1,response.result);
                    mvpRecommendTopicView.onDataSetChanged();
                }
            }
        });
        }
    }
}
