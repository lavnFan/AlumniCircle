package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.content.Context;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicShareItem;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class CircleDynamicShareItemAdapter extends BasisAdapter<DynamicShareItem,CircleDynamicShareItemAdapter.viewHolder> {

    public CircleDynamicShareItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicShareItem>(),viewHolder.class);
    }

    public CircleDynamicShareItemAdapter(Context mContext, List<DynamicShareItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(viewHolder holder, DynamicShareItem entity) {

    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {

    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_circle_dynamic_text_comment;
    }

    public static class viewHolder{

    }
}
