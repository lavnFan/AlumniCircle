package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.seu.wufan.alumnicircle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author wufan
 * @date 2016/2/3
 */
public class CircleTopicRecycleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_topic_dynamic, null);
            return new ViewHolderDynamic(v);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_circle_topic_0_type, null);
            return new ViewHolderTopic(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderDynamic){
            ((ViewHolderDynamic) holder).bindView(position);
        }else{
            ((ViewHolderTopic) holder).bindView(position);
        }

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0)?0:1;
    }

    public static class ViewHolderDynamic extends RecyclerView.ViewHolder{
        @Bind(R.id.circle_topic_hot_ll)
        LinearLayout hotLl;
        @Bind(R.id.circle_topic_hot_view)
        View hotView;

        public ViewHolderDynamic(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindView(int position){
            if(position==1){
                hotLl.setVisibility(View.VISIBLE);
                hotView.setVisibility(View.VISIBLE);
            }

        }
    }

    public static class ViewHolderTopic extends RecyclerView.ViewHolder{

        public ViewHolderTopic(View itemView) {
            super(itemView);
        }

        public void bindView(int position){

        }
    }

}
