package com.seu.wufan.alumnicircle.ui.adapter.me;

import android.content.Context;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/14
 */
public class CollectionItemAdapter extends BasisAdapter<DynamicItem,CollectionItemAdapter.viewHolder> {

    public CollectionItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicItem>(), viewHolder.class);
    }

    public CollectionItemAdapter(Context mContext, List<DynamicItem> mEntities, Class<viewHolder> classType) {
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
        return R.layout.list_item_collection;
    }

    public static class viewHolder{

    }
}
