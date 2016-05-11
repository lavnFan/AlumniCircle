package com.umeng.comm.ui.fragments;

import android.content.Intent;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.TopicDetailActivity;
import com.umeng.common.ui.adapters.SearchTopicAdapter;
import com.umeng.common.ui.fragments.SearchTopicBaseFragment;
import com.umeng.common.ui.listener.TopicToTopicDetail;


/**
 * Created by wangfei on 15/12/8.
 */
public class SearchTopicFragment extends SearchTopicBaseFragment {


    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_search_topic");
    }

    @Override
    public void setAdaptertoFetail() {
        ((SearchTopicAdapter)mTopicAdapter).setTtt(new TopicToTopicDetail() {
            @Override
            public void gotoTopicDetail(Topic topic) {
                                Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
                intent.putExtra(Constants.TAG_TOPIC,topic);
                getActivity().startActivity(intent);
            }
        });
    }


}
