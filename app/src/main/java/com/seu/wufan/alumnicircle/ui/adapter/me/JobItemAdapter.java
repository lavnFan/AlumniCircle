package com.seu.wufan.alumnicircle.ui.adapter.me;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/15
 */
public class JobItemAdapter extends BasisAdapter<Job,JobItemAdapter.ViewHolder>{

    public JobItemAdapter(Context mContext) {
        super(mContext, new ArrayList<Job>(), ViewHolder.class);
    }

    public JobItemAdapter(Context mContext, List<Job> mEntities, Class<ViewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(JobItemAdapter.ViewHolder holder, Job entity) {
        holder.mCompanyTv.setText(entity.getCompany());
        holder.mTimeTv.setText(entity.getDuration());
    }

    @Override
    protected void initViewHolder(View convertView, JobItemAdapter.ViewHolder holder) {
        holder.mCompanyTv = (TextView) convertView.findViewById(R.id.item_me_show_left_tv);
        holder.mTimeTv = (TextView) convertView.findViewById(R.id.item_me_show_right_tv);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_me_show;
    }

    public static class ViewHolder {
        private TextView mCompanyTv;
        private TextView mTimeTv;
    }
}
