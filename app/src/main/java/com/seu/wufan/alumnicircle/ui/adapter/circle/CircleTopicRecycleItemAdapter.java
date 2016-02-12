package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seu.wufan.alumnicircle.R;

/**
 * @author wufan
 * @date 2016/2/3
 */
public class CircleTopicRecycleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_topic_dynamic, null);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        public viewHolder(View itemView) {
            super(itemView);
        }
    }

}
