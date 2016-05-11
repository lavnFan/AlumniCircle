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

package com.umeng.common.ui.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.FollowedUserPresenter;
import com.umeng.common.ui.widgets.RoundImageView;


/**
 * 点赞用户的ViewHolder
 */
public class FollowedUserViewHolder extends ViewHolder implements View.OnClickListener{

    public RoundImageView mImageView;
    public TextView mNameTextView;
    public TextView mProfileTextView;
    public TextView mFansCountView;
    public ImageView mFollowStateBtn;

    private CommUser mCommUser;

    private FollowedUserPresenter mPresenter;

    public FollowedUserViewHolder(Context context, boolean isCurrentUser){
        if(isCurrentUser){
            mPresenter = new FollowedUserPresenter(context);
        }
    }

    @Override
    protected void initWidgets() {
        mImageView = findViewById(ResFinder
                .getId("umeng_comm_user_picture"));
        mNameTextView = findViewById(ResFinder
                .getId("umeng_comm_user_name"));
        mProfileTextView = findViewById(ResFinder
                .getId("umeng_comm_user_profile"));
        mFollowStateBtn = findViewById(ResFinder
                .getId("umeng_comm_user_follow_state"));
        mFansCountView = findViewById(ResFinder.getId("umeng_comm_fans_count"));
        mFollowStateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(mPresenter == null){
            return;
        }
        if(mCommUser == null){
            return;
        }
        if(mCommUser.isFollowed){
            mPresenter.cancelFollowUser(mCommUser);
        }else{
            mPresenter.followUser(mCommUser);
        }
    }

    public void setUser(CommUser user){
        mCommUser = user;
    }

    @Override
    protected int getItemLayout() {
        return ResFinder.getLayout("umeng_comm_friends_lv_item");
    }

}
