package com.seu.wufan.alumnicircle.ui.adapter.contacts;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.ContactsFriendsItem;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class ContactsAlumniGoodItemAdapter extends BasisAdapter<ContactsFriendsItem, ContactsAlumniGoodItemAdapter.viewHolder> {

    public ContactsAlumniGoodItemAdapter(Context mContext) {
        super(mContext, new ArrayList<ContactsFriendsItem>(), viewHolder.class);
    }

    public ContactsAlumniGoodItemAdapter(Context mContext, List<ContactsFriendsItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(viewHolder holder, ContactsFriendsItem entity) {

    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {
        holder.welcomeTv = (TextView) convertView.findViewById(R.id.contacts_friend_item_welcome_tv);
        holder.watchBtn = (Button) convertView.findViewById(R.id.contacts_friends_item_btn);

        holder.welcomeTv.setVisibility(View.GONE);
        holder.watchBtn.setText(R.string.watch);

    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_contacts_alumni_good;
    }

    public static class viewHolder {
        private TextView welcomeTv;
        private Button watchBtn;
    }
}
