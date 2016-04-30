package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/1
 */
public class DynamicItemAdapter extends BasisAdapter<DynamicItem, DynamicItemAdapter.viewHolder> {


    ImageAdapter imageAdapter;

    public DynamicItemAdapter(Context mContext) {
        super(mContext, new ArrayList<DynamicItem>(), viewHolder.class);
    }

    public DynamicItemAdapter(Context mContext, List<DynamicItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(viewHolder holder, DynamicItem entity) {

        ArrayList<String> selectedPhotos = new ArrayList<>();
        selectedPhotos.add(0, "http://img4.imgtn.bdimg.com/it/u=2015527637,3623972403&fm=21&gp=0.jpg");
        selectedPhotos.add(1, "http://img1.imgtn.bdimg.com/it/u=3527020364,2054046693&fm=23&gp=0.jpg");
        int columns = selectedPhotos.size();
        columns = (columns>2)?3:columns;
        holder.gridViewImages.setNumColumns(columns);
        imageAdapter = new ImageAdapter(getmContext(),selectedPhotos);
        holder.gridViewImages.setAdapter(imageAdapter);

        holder.gridViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Images:",position+"");
            }
        });
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {
        holder.personInfoRl = (RelativeLayout) convertView.findViewById(R.id.circle_dynamic_item_person_info_relative_layout);
        holder.gridViewImages = (MyGridView) convertView.findViewById(R.id.circle_dynamic_list_card_view_grid_view);
        holder.gridViewImages.setFocusable(false);

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

    public static class viewHolder {
        RelativeLayout personInfoRl;
        MyGridView gridViewImages;
    }
}