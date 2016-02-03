package com.seu.wufan.alumnicircle.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.DynamicItem;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/1
 */
public class DynamicItemAdapter extends BasisAdapter<DynamicItem,DynamicItemAdapter.viewHolder> {

    public DynamicItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicItem>(),viewHolder.class);
    }
    public DynamicItemAdapter(Context mContext, List<DynamicItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(viewHolder holder, DynamicItem entity) {

    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {

    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_dynamic;
    }

    public static class viewHolder{

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}