package com.seu.wufan.alumnicircle.ui.adapter.me;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/16
 */
public class EduItemAdapter extends BasisAdapter<Edu,EduItemAdapter.ViewHolder> {

    public EduItemAdapter(Context mContext) {
        super(mContext, new ArrayList<Edu>(), ViewHolder.class);
    }

    public EduItemAdapter(Context mContext, List<Edu> mEntities, Class<ViewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(EduItemAdapter.ViewHolder holder, Edu entity) {
        holder.mSchoolTv.setText(entity.getSchool());
        holder.mTimeTv.setText(entity.getDuration());
    }

    @Override
    protected void initViewHolder(View convertView, EduItemAdapter.ViewHolder holder) {
        holder.mSchoolTv = (TextView) convertView.findViewById(R.id.item_me_show_left_tv);
        holder.mTimeTv = (TextView) convertView.findViewById(R.id.item_me_show_right_tv);
    }

    @Override
    public int getItemLayout() {
        return R.layout.item_me_show;
    }

    public static class ViewHolder {
        private TextView mSchoolTv;
        private TextView mTimeTv;
    }
}
