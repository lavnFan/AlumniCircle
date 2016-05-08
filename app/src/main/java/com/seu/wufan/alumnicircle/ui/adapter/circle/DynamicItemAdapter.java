package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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

        holder.mNameTv.setText(entity.getName());
        holder.mCollegeTv.setText(entity.getUser_job());
        holder.mProTv.setText(entity.getUser_major());
        holder.mTextTv.setText(entity.getNews_text());
        holder.mTimeTv.setText(entity.getPost_time());
        holder.mAgreeTv.setText(entity.getLike_amount());
        holder.mCommentTv.setText(entity.getComment_amount());
        holder.mForwardTv.setText(entity.getShare_amount());

        CommonUtils.showCircleImageWithGlide(getmContext(), holder.mPhotoCv, entity.getUser_image());

        ArrayList<String> selectedPhotos = (ArrayList<String>) entity.getImages();
        if (selectedPhotos!=null) {
//        ArrayList<String> selectedPhotos = new ArrayList<>();
//        selectedPhotos.add(0, "http://img4.imgtn.bdimg.com/it/u=2015527637,3623972403&fm=21&gp=0.jpg");
//        selectedPhotos.add(1, "http://img1.imgtn.bdimg.com/it/u=3527020364,2054046693&fm=23&gp=0.jpg");
            int columns = selectedPhotos.size();
            columns = (columns > 2) ? 3 : columns;
            holder.gridViewImages.setNumColumns(columns);
            imageAdapter = new ImageAdapter(getmContext(), selectedPhotos);
            holder.gridViewImages.setAdapter(imageAdapter);
            holder.gridViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("Images:", position + "");
                }
            });
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {
        holder.personInfoRl = (RelativeLayout) convertView.findViewById(R.id.circle_dynamic_item_person_info_relative_layout);
        holder.gridViewImages = (MyGridView) convertView.findViewById(R.id.circle_dynamic_list_card_view_grid_view);
        holder.mPhotoCv = (CircleImageView) convertView.findViewById(R.id.circle_dynamic_item_photo_cv);
        holder.mNameTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_name_tv);
        holder.mCollegeTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_college_tv);
        holder.mProTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_pro_tv);
        holder.mTimeTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_time_tv);
        holder.mTextTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_text_tv);
        holder.mAgreeTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_agree_count_tv);
        holder.mCommentTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_comment_count_tv);
        holder.mForwardTv = (TextView) convertView.findViewById(R.id.circle_dynamic_item_forward_count_tv);

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
        CircleImageView mPhotoCv;
        TextView mNameTv;
        TextView mProTv;
        TextView mCollegeTv;
        TextView mTimeTv;
        TextView mTextTv;
        TextView mCommentTv;
        TextView mForwardTv;
        ;
        TextView mAgreeTv;
    }
}