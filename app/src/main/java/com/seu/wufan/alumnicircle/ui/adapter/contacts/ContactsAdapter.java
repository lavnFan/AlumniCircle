package com.seu.wufan.alumnicircle.ui.adapter.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.umeng.message.proguard.T;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/5/20
 */
public class ContactsAdapter extends BasisAdapter<Friend,ContactsAdapter.ViewHolder> implements SectionIndexer{

    public ContactsAdapter(Context mContext) {
        super(mContext, new ArrayList<Friend>(), ViewHolder.class);
    }
    public ContactsAdapter(Context mContext, List<Friend> mEntities, Class<ViewHolder> classType) {
        super(mContext, mEntities, classType);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Friend mContent = getmEntities().get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getmContext()).inflate(R.layout.list_item_contacts, null);
            initViewHolder(convertView, viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        Friend entity = getmEntities().get(position);
        setDataIntoView(viewHolder, entity);
        return convertView;
    }

    @Override
    protected void setDataIntoView(ViewHolder holder, Friend entity) {
        CommonUtils.showCircleImageWithGlide(getmContext(),holder.mCv,entity.getImage());
        holder.tvName.setText(entity.getName());
    }

    @Override
    protected void initViewHolder(View convertView, ViewHolder holder) {
        holder.mCv = (CircleImageView) convertView.findViewById(R.id.frienduri);
        holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
        holder.tvName = (TextView) convertView.findViewById(R.id.friendname);
    }

    @Override
    public int getItemLayout() {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getmEntities().get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
       return  getmEntities().get(position).getLetters().charAt(0);
    }

    public class ViewHolder {
        TextView tvLetter;
        TextView tvName;
        CircleImageView mCv;
    }
}
