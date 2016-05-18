package com.seu.wufan.alumnicircle.ui.adapter.me;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/17
 */
public class JobMyItemAdapter extends BasisAdapter<Job,JobMyItemAdapter.ViewHolder> {

    public JobMyItemAdapter(Context mContext) {
        super(mContext, new ArrayList<Job>(), ViewHolder.class);
    }

    public JobMyItemAdapter(Context mContext, List<Job> mEntities, Class<ViewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(JobMyItemAdapter.ViewHolder holder, Job entity) {
        holder.mCompanyTv.setText(entity.getCompany());
        holder.mTimeTv.setText(entity.getDuration());
        holder.mJobTv.setText(entity.getJob());
    }

    @Override
    protected void initViewHolder(View convertView, JobMyItemAdapter.ViewHolder holder) {
        holder.mCompanyTv = (TextView) convertView.findViewById(R.id.item_info_job_company_tv);
        holder.mTimeTv = (TextView) convertView.findViewById(R.id.item_info_job_duration_tv);
        holder.mJobTv = (TextView) convertView.findViewById(R.id.item_info_job_tv);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_my_info_job;
    }

    public static class ViewHolder {
        private TextView mCompanyTv;
        private TextView mTimeTv;
        private TextView mJobTv;
    }
}
