package com.seu.wufan.alumnicircle.ui.adapter.contacts;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.SearchFriendItem;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/5/22
 */
public class SearchFriendAdapter extends BasisAdapter<SearchFriendItem,SearchFriendAdapter.ViewHolder>{

    public SearchFriendAdapter(Context mContext) {
        super(mContext, new ArrayList<SearchFriendItem>(), ViewHolder.class);
    }

    public SearchFriendAdapter(Context mContext, List<SearchFriendItem> mEntities, Class<ViewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(SearchFriendAdapter.ViewHolder holder, SearchFriendItem entity) {
        CommonUtils.showCircleImageWithGlide(getmContext(),holder.mCv,entity.getImage());
        holder.mSchoolTv.setText(entity.getUser_school());
        holder.mNameTv.setText(entity.getName());
    }

    @Override
    protected void initViewHolder(View convertView, SearchFriendAdapter.ViewHolder holder) {
        holder.mCv = (CircleImageView) convertView.findViewById(R.id.item_search_cv);
        holder.mNameTv = (TextView) convertView.findViewById(R.id.item_search_name_tv);
        holder.mSchoolTv = (TextView) convertView.findViewById(R.id.item_search_school_tv);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_search_friends;
    }

    public static class ViewHolder {
        private CircleImageView mCv;
        private TextView mNameTv;
        private TextView mSchoolTv;
    }
}
