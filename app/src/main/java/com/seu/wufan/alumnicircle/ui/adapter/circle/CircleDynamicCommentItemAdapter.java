package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.item.DynamicCommentItem;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class CircleDynamicCommentItemAdapter extends BasisAdapter<DynamicCommentItem, CircleDynamicCommentItemAdapter.viewHolder> {

    public CircleDynamicCommentItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicCommentItem>(), viewHolder.class);
    }

    public CircleDynamicCommentItemAdapter(Context mContext, List<DynamicCommentItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(viewHolder holder, DynamicCommentItem entity) {

    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {
        holder.hideTv = (TextView) convertView.findViewById(R.id.circle_dynamic_text_item_concrete);


        holder.hideTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_circle_dynamic_text_comment;
    }

    public static class viewHolder {
        TextView hideTv;

    }
}
