package com.seu.wufan.alumnicircle.ui.adapter.me;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/17
 */
public class EduMyItemAdapter extends BasisAdapter<Edu,EduMyItemAdapter.ViewHolder> {

    public EduMyItemAdapter(Context mContext) {
        super(mContext, new ArrayList<Edu>(), ViewHolder.class);
    }

    public EduMyItemAdapter(Context mContext, List<Edu> mEntities, Class<ViewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(EduMyItemAdapter.ViewHolder holder, Edu entity) {
        holder.mSchoolTv.setText(entity.getSchool());
        holder.mTimeTv.setText(entity.getDuration());
        String major_degree = entity.getMajor()+" "+entity.getDegree();
        holder.mMajorDegreeTv.setText(major_degree);
    }

    @Override
    protected void initViewHolder(View convertView, EduMyItemAdapter.ViewHolder holder) {
        holder.mSchoolTv = (TextView) convertView.findViewById(R.id.item_edu_school_tv);
        holder.mTimeTv = (TextView) convertView.findViewById(R.id.item_edu_duration_tv);
        holder.mMajorDegreeTv = (TextView) convertView.findViewById(R.id.item_edu_major_degree_tv);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_my_info_edu;
    }

    public static class ViewHolder {
        private TextView mSchoolTv;
        private TextView mTimeTv;
        private TextView mMajorDegreeTv;
    }
}
