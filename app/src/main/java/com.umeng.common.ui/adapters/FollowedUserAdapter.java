/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.common.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.viewholders.FollowedUserViewHolder;


/**
 * 对某条Feed点赞的用户列表Adapter
 */
public class FollowedUserAdapter extends CommonAdapter<CommUser, FollowedUserViewHolder> {

    protected boolean mIsCurrentUser;

    private String mFansStr;
    private String mPostFeedStr;

    private String mTargetClassName;

    public FollowedUserAdapter(Context context) {
        super(context);
        mPostFeedStr = ResFinder.getString("umeng_comm_user_post_feed");
        mFansStr = ResFinder.getString("umeng_comm_user_fans");
    }

    public void setIsCurrentUser(boolean isCurrentUser){
        this.mIsCurrentUser = isCurrentUser;
    }

    @Override
    protected FollowedUserViewHolder createViewHolder() {
        FollowedUserViewHolder holder = new FollowedUserViewHolder(mContext, mIsCurrentUser);
        return holder;
    }

    @Override
    protected void setItemData(int position, FollowedUserViewHolder holder, View rootView) {
        final CommUser user = getItem(position);
        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
        holder.mImageView.setImageUrl(user.iconUrl, option);
        if(mContext instanceof Activity){
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startUserInfoActivity(user);
                }
            });
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startUserInfoActivity(user);
                }
            });
        }

        holder.mNameTextView.setText(TextUtils.isEmpty(user.name) ? "" : user.name);
        holder.setUser(user);

        String postFeedCount = mPostFeedStr + CommonUtils.getLimitedCount(user.feedCount);
        holder.mProfileTextView.setText(postFeedCount);

        String fansCount = mFansStr + CommonUtils.getLimitedCount(user.fansCount);
        holder.mFansCountView.setText(fansCount);

        if (!mIsCurrentUser || user.permisson == CommUser.Permisson.SUPPER_ADMIN) {
            holder.mFollowStateBtn.setVisibility(View.GONE);
        } else {
            boolean isFollowed = user.isFollowed;
            boolean isFollowing = user.isFollowingMe;
            if (isFollowed && isFollowing) {
                holder.mFollowStateBtn.setImageDrawable(ResFinder.getDrawable("xianghu"));
            } else if (isFollowed) {
                holder.mFollowStateBtn.setImageDrawable(ResFinder.getDrawable("yiguanzhu"));
            } else {
                holder.mFollowStateBtn.setImageDrawable(ResFinder.getDrawable("jiaguanzhu"));
            }
            holder.mFollowStateBtn.setVisibility(View.VISIBLE);
        }
    }

    protected void startUserInfoActivity(CommUser user){
        Intent intent = new Intent();
        if(TextUtils.isEmpty(mTargetClassName)){
            mTargetClassName = mContext.getClass().getName();
        }
        intent.setClassName(mContext, mTargetClassName);
        intent.putExtra(Constants.TAG_USER, user);
        mContext.startActivity(intent);
    }

    public void setTargetClassName(String targetClassName){
        this.mTargetClassName = targetClassName;
    }
}
