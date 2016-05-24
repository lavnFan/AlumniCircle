package com.seu.wufan.alumnicircle.ui.adapter.contacts;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.activity.contacts.SearchFriendActivity;

import java.util.List;

/**
 * @author wufan
 * @date 2016/5/22
 */
public class SearchHistoryAdapter extends BasisAdapter<String,SearchHistoryAdapter.ViewHolder> {

    public SearchHistoryAdapter(Context mContext, List<String> mEntities, Class classType ){
        super(mContext,mEntities,classType);
    }

    @Override
    protected void setDataIntoView(SearchHistoryAdapter.ViewHolder holder, String entity) {
        holder.searchHistoryTv.setText(entity);
    }

    @Override
    protected void initViewHolder(View convertView, SearchHistoryAdapter.ViewHolder holder) {
        holder.searchHistoryTv = (TextView)convertView.findViewById(R.id.search_history_item_text);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_search_history;
    }

    public static class ViewHolder{
        TextView searchHistoryTv;
    }
}