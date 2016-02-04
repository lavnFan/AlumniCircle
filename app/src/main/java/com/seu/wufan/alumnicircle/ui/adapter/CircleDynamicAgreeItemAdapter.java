package com.seu.wufan.alumnicircle.ui.adapter;

import android.content.Context;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.item.DynamicAgreeItem;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class CircleDynamicAgreeItemAdapter extends BasisAdapter<DynamicAgreeItem,CircleDynamicAgreeItemAdapter.viewHolder> {

    public CircleDynamicAgreeItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicAgreeItem>(),viewHolder.class);
    }

    public CircleDynamicAgreeItemAdapter(Context mContext, List<DynamicAgreeItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(CircleDynamicAgreeItemAdapter.viewHolder holder, DynamicAgreeItem entity) {

    }

    @Override
    protected void initViewHolder(View convertView, CircleDynamicAgreeItemAdapter.viewHolder holder) {

    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_circle_dynamic_text_comment;
    }

    public static class viewHolder{

    }
}
