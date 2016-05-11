package com.umeng.comm.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.HotTopicFeedPresenter;
import com.umeng.common.ui.util.Filter;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wangfei on 16/1/22.
 */
public class HotTopicFeedFragment extends TopicFeedFragment{
    private TextView button1,button2,button3,button4;
    private int hottype = 4;
    public static HotTopicFeedFragment newTopicFeedFrmg(final Topic topic) {
        HotTopicFeedFragment topicFeedFragment = new HotTopicFeedFragment();
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
    protected HotTopicFeedPresenter createPresenters() {
        HotTopicFeedPresenter presenter = new HotTopicFeedPresenter(this);
        presenter.setId(mTopic.id);
        return presenter;
    }
    public void initSwitchView(){
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_switch_button"), null);

        button1 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
        button2 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
        button3 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
        button4 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
        button4.setSelected(true);
        button1.setOnClickListener(switchListener);
        button2.setOnClickListener(switchListener);
        button3.setOnClickListener(switchListener);
        button4.setOnClickListener(switchListener);
        mLinearLayout.addView(headerView, 0);
    }
    private View.OnClickListener switchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == ResFinder.getId("umeng_switch_button_one")){
                boolean isneed = false;
                if (hottype!=0)
                {
                    isneed = true;
                }
                hottype = 0;
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(false);
                ChangeFragment(hottype,isneed);
            }else if(view.getId() == ResFinder.getId("umeng_switch_button_two")){
                boolean isneed = false;
                if (hottype!=1)
                {
                    isneed = true;
                }
                hottype = 1;
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
                button4.setSelected(false);
                ChangeFragment(hottype, isneed);
            }else if(view.getId() == ResFinder.getId("umeng_switch_button_three")){
                boolean isneed = false;
                if (hottype!=2)
                {
                    isneed = true;
                }
                hottype = 2;
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
                button4.setSelected(false);
                ChangeFragment(hottype, isneed);
            }else if(view.getId() == ResFinder.getId("umeng_switch_button_four")){
                boolean isneed = false;
                if (hottype!=3)
                {
                    isneed = true;
                }
                hottype = 3;
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(true);
                ChangeFragment(hottype,isneed);
            }
        }
    };
    @Override
    protected void initWidgets() {
        super.initWidgets();
//        BroadcastUtils.unRegisterBroadcast(getActivity(), mReceiver);
        findViewById(ResFinder.getId("umeng_comm_new_post_btn")).setVisibility(View.GONE);
        initSwitchView();
    }

    public void ChangeFragment(int hottype,boolean isneedsavenextpage){

        if (mPresenter!=null) {
            if (isneedsavenextpage){
                mPresenter.setIsNeedRemoveOldFeeds();
            }
            ((HotTopicFeedPresenter)mPresenter).loadDataFromServer(hottype);
        }
    }
    @Override
    protected void postFeedComplete(FeedItem feedItem) {

    }
    @Override
    protected void deleteFeedComplete(FeedItem feedItem) {
        mFeedLvAdapter.getDataSource().remove(feedItem);
        mFeedLvAdapter.notifyDataSetChanged();
        updateForwardCount(feedItem, -1);
        Log.d(getTag(), "### 删除feed");
    }

    @Override
    protected void addPaddingToListView() {
    }
}

