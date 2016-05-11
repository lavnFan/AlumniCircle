package com.umeng.comm.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.UserInfoActivity;
import com.umeng.comm.ui.adapters.viewholders.FeedLikeViewHolder;
import com.umeng.common.ui.adapters.CommonAdapter;


/**
 * Created by wangfei on 16/1/25.
 */
public class FeedLikeAdapter extends CommonAdapter<Like, FeedLikeViewHolder> {
    @Override
    protected FeedLikeViewHolder createViewHolder() {
        return new FeedLikeViewHolder();
    }

    @Override
    protected void setItemData(int position, FeedLikeViewHolder viewHolder, View rootView) {
        final Like like = getItem(position);
        viewHolder.usericon.setImageDrawable(ResFinder.getDrawable("morentuf"));
        viewHolder.usericon.setImageUrl(like.creator.iconUrl);
        viewHolder.username.setText(like.creator.name);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyInformationActivity.class);
                intent.putExtra(Constants.TAG_USER, like.creator);
                mContext.startActivity(intent);
            }
        });
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyInformationActivity.class);
                intent.putExtra(Constants.TAG_USER, like.creator);
                mContext.startActivity(intent);
            }
        });
        viewHolder.usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyInformationActivity.class);
                intent.putExtra(Constants.TAG_USER, like.creator);
                mContext.startActivity(intent);
            }
        });

    }

    public FeedLikeAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

}
