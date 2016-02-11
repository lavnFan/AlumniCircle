package com.seu.wufan.alumnicircle.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.item.DynamicItem;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/1
 */
public class DynamicItemAdapter extends BasisAdapter<DynamicItem, DynamicItemAdapter.viewHolder> {

    public DynamicItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicItem>(), viewHolder.class);
    }

    public DynamicItemAdapter(Context mContext, List<DynamicItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(viewHolder holder, DynamicItem entity) {

    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {
        holder.personInfoRl = (RelativeLayout) convertView.findViewById(R.id.circle_dynamic_item_person_info_relative_layout);


        holder.personInfoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getmContext(), MyInformationActivity.class);
                getmContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_dynamic;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public static class viewHolder {
        RelativeLayout personInfoRl;
    }
}