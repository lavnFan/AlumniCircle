package com.seu.wufan.alumnicircle.ui.adapter.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.ContactsFriendsItem;
import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.activity.contacts.NewFriendsActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.umeng.comm.core.beans.CommUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class ContactsFriendsItemAdapter extends BasisAdapter<FriendRequestItem, ContactsFriendsItemAdapter.viewHolder> {

    FriendMessage friendMessage;

    public ContactsFriendsItemAdapter(Context mContext) {
        super(mContext, new ArrayList<FriendRequestItem>(), viewHolder.class);
    }

    public ContactsFriendsItemAdapter(Context mContext, List<FriendRequestItem> mEntities, Class<viewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    protected void setDataIntoView(final viewHolder holder, final FriendRequestItem entity) {
        CommonUtils.showCircleImageWithGlide(getmContext(), holder.mCv, entity.getImage());
        holder.mCompanyTv.setText(entity.getSchool());
        holder.mJobTv.setText(entity.getMajor());
        holder.mNameTv.setText(entity.getName());
        holder.mWelcomeTv.setText(entity.getAdd_friend_text());
        holder.mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendMessage.acceptRequest(entity.getUser_id(),holder.mSendBtn);
            }
        });
        holder.mItemRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String[] str = {"删除"};
                final AlertDialog dialog = new AlertDialog.Builder(getmContext())
                        .setItems(str, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                friendMessage.deleteRequest(entity.getUser_id());
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return false;
            }
        });
        holder.mItemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommUser user = new CommUser();
                user.sourceUid = entity.getUser_id();
                Intent intent = new Intent(getmContext(),
                        MyInformationActivity.class);
                intent.putExtra(com.umeng.comm.core.constants.Constants.TAG_USER, user);
                getmContext().startActivity(intent);
            }
        });
    }

    @Override
    protected void initViewHolder(View convertView, viewHolder holder) {
        holder.mCv = (CircleImageView) convertView.findViewById(R.id.item_contacts_friend_request_cv);
        holder.mCompanyTv = (TextView) convertView.findViewById(R.id.item_contacts_friend_request_company_tv);
        holder.mJobTv = (TextView) convertView.findViewById(R.id.item_contacts_friend_request_job_tv);
        holder.mNameTv = (TextView) convertView.findViewById(R.id.item_contacts_friend_request_name_tv);
        holder.mWelcomeTv = (TextView) convertView.findViewById(R.id.contacts_friend_item_welcome_tv);
        holder.mSendBtn = (Button) convertView.findViewById(R.id.contacts_friends_item_btn);
        holder.mItemRl = (RelativeLayout) convertView.findViewById(R.id.item_contacts_new_friends_rl);
    }

    @Override
    public int getItemLayout() {
        return R.layout.list_item_contacts_new_friends;
    }

    public static class viewHolder {
        private CircleImageView mCv;
        private TextView mNameTv;
        private TextView mCompanyTv;
        private TextView mJobTv;
        private TextView mWelcomeTv;
        private Button mSendBtn;
        private RelativeLayout mItemRl;
    }

    public void setFriendMessage(FriendMessage friendMessage) {
        this.friendMessage = friendMessage;
    }

    public interface FriendMessage {
        void acceptRequest(String user_id,Button sendBtn);

        void deleteRequest(String user_id);
    }

}
