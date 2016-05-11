package com.umeng.comm.ui.fragments;

import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.RecommendTopicFeedPresenter;
import com.umeng.common.ui.util.Filter;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wangfei on 16/1/22.
 */
public class RecommendTopicFeedFragment extends TopicFeedFragment{
    public static RecommendTopicFeedFragment newTopicFeedFrmg(final Topic topic) {
        RecommendTopicFeedFragment topicFeedFragment = new RecommendTopicFeedFragment();
        topicFeedFragment.mTopic = topic;
        topicFeedFragment.mFeedFilter = new Filter<FeedItem>() {

            @Override
            public List<FeedItem> doFilte(List<FeedItem> newItems) {
                if (newItems == null || newItems.size() == 0) {
                    return newItems;
                }
                Iterator<FeedItem> iterator = newItems.iterator();
                while (iterator.hasNext()) {
                    List<Topic> topics = iterator.next().topics;
                    if (!topics.contains(topic)) {
                        iterator.remove();
                    }
                }
                return newItems;
            }
        };
        return topicFeedFragment;
    }

    @Override
    protected RecommendTopicFeedPresenter createPresenters() {
        RecommendTopicFeedPresenter presenter = new RecommendTopicFeedPresenter(this);
        presenter.setId(mTopic.id);
        return presenter;
    }
    @Override
    protected void initWidgets() {
        super.initWidgets();
        findViewById(ResFinder.getId("umeng_comm_new_post_btn")).setVisibility(View.GONE);
    }
    @Override
    protected void postFeedComplete(FeedItem feedItem) {

    }
}
