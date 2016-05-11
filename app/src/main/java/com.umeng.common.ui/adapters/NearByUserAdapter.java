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
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.viewholders.FollowedUserViewHolder;
import com.umeng.common.ui.adapters.viewholders.NearByUserViewHolder;
import com.umeng.common.ui.fragments.NearByUserFragment;


/**
 * 对某条Feed点赞的用户列表Adapter
 */
public class NearByUserAdapter extends FollowedUserAdapter {

    public NearByUserAdapter(Context context) {
        super(context);
    }

    @Override
    protected FollowedUserViewHolder createViewHolder() {
        FollowedUserViewHolder holder = new NearByUserViewHolder(mContext, mIsCurrentUser);
        return holder;
    }

    @Override
    protected void setItemData(int position, FollowedUserViewHolder holder, View rootView) {
        super.setItemData(position, holder, rootView);
        final CommUser user = getItem(position);
        NearByUserViewHolder viewHolder = (NearByUserViewHolder) holder;
        if (user.gender != null) {
            viewHolder.mUserGender.setVisibility(View.VISIBLE);
            if (user.gender == CommUser.Gender.FEMALE) {
                viewHolder.mUserGender.setImageResource(ResFinder.getResourceId(
                        ResFinder.ResType.DRAWABLE, "umeng_comm_gender_female"));
            } else if (user.gender == CommUser.Gender.FEMALE) {
                viewHolder.mUserGender.setImageResource(ResFinder.getResourceId(
                        ResFinder.ResType.DRAWABLE, "umeng_comm_gender_male"));
            }
        } else {
            viewHolder.mUserGender.setVisibility(View.GONE);
        }

        int distance = (int)user.extraData.getDouble(HttpProtocol.DISTANCE_KEY);
        String formatDistance = CommonUtils.formatDistance(distance);
        viewHolder.mUserDistance.setText(formatDistance);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserInfoActivity(user);
            }
        });
    }
}
