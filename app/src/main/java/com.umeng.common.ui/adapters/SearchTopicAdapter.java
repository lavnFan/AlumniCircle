package com.umeng.common.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.listener.TopicToTopicDetail;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.widgets.RoundImageView;

import java.util.List;

/**
 * Created by umeng on 12/10/15.
 */
public class SearchTopicAdapter extends RecyclerView.Adapter<SearchTopicAdapter.TopicViewHolder>{

    public static class TopicViewHolder extends RecyclerView.ViewHolder{
        public View mRootView;
        public RoundImageView mTopicIcon;
        public TextView mTopicTv;
        public TextView mDescTv;
        public ToggleButton mFollowedBtn;


        public  TopicViewHolder(View view){
            super(view);
            mRootView = view;
            mTopicTv = (TextView)view.findViewById(ResFinder
                    .getId("umeng_comm_topic_tv"));
            mDescTv = (TextView)view.findViewById(ResFinder
                    .getId("umeng_comm_topic_desc_tv"));
            mFollowedBtn = (ToggleButton)view.findViewById(ResFinder
                    .getId("umeng_comm_topic_togglebutton"));
            mTopicIcon = (RoundImageView)view.findViewById(ResFinder
                    .getId("umeng_comm_topic_icon"));
        }
    }
    private TopicToTopicDetail ttt;
    private List<Topic> topicList;
    private CommunitySDK mCommunitySDK;
    private Context context;

    public SearchTopicAdapter(List<Topic> topicList, Context context){
        this.topicList=topicList;
        mCommunitySDK = CommunityFactory.getCommSDK(context);
        this.context = context;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(ResFinder.getLayout("umeng_commm_followed_topic_lv_item"), viewGroup, false);
        return new TopicViewHolder(rootView);
    }

    public void setTtt(TopicToTopicDetail ttt) {
        this.ttt = ttt;
    }

    @Override
    public void onBindViewHolder(final TopicViewHolder topicViewHolder, final int i) {
        Topic item = topicList.get(i);
        topicViewHolder.mTopicTv.setText(item.name);
        topicViewHolder.mTopicIcon.setImageUrl(item.icon);
        topicViewHolder.mDescTv.setText(item.desc);
        topicViewHolder.mFollowedBtn.setChecked(!item.isFocused);
        topicViewHolder.mFollowedBtn.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void doAfterLogin(View v) {
                // 如用户没有关注这个论坛
                if(!topicList.get(i).isFocused){
                mCommunitySDK.followTopic(topicList.get(i), new Listeners.SimpleFetchListener<Response>() {
                    @Override
                    public void onComplete(Response response) {
                        if ( NetworkUtils.handleResponseComm(response) ) {
                            topicList.get(i).isFocused=true;
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_follow_failed");
                            return ;
                        }
                        if(response.errCode!= ErrorCode.NO_ERROR){
                            topicViewHolder.mFollowedBtn.setChecked(!topicViewHolder.mFollowedBtn.isChecked());
                        }
                        else{
                            Topic topic = topicList.get(i);
                            topic.isFocused=true;
                            BroadcastUtils.sendTopicFollowBroadcast(context, topic);
                        }
                    }
                });
                // 如用户已经关注了这个论坛
                }else{
                    mCommunitySDK.cancelFollowTopic(topicList.get(i), new Listeners.SimpleFetchListener<Response>() {
                        @Override
                        public void onComplete(Response response) {
                            if ( NetworkUtils.handleResponseComm(response) ) {
                                topicList.get(i).isFocused=false;
                                ToastMsg.showShortMsgByResName("umeng_comm_topic_cancel_failed");
                                return ;
                            }
                            if (response.errCode != ErrorCode.NO_ERROR) {
                                topicViewHolder.mFollowedBtn.setChecked(!topicViewHolder.mFollowedBtn.isChecked());
                            } else {
                                Topic topic = topicList.get(i);
                                topic.isFocused=false;
                                BroadcastUtils.sendTopicCancelFollowBroadcast(context, topic);
                            }
                        }
                    });
                }
            }
        });

        topicViewHolder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttt.gotoTopicDetail(topicList.get(i));
//                Intent intent = new Intent(context, TopicDetailActivity.class);
//                intent.putExtra(Constants.TAG_TOPIC,topicList.get(i));
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}
