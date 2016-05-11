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

import android.content.Context;
import android.view.View;

import com.umeng.comm.core.beans.MessageSession;
import com.umeng.comm.core.utils.TimeUtils;
import com.umeng.common.ui.adapters.viewholders.MessageSessionViewHolder;


public class MessageSessionAdapter extends CommonAdapter<MessageSession, MessageSessionViewHolder> {

    public MessageSessionAdapter(Context context) {
        super(context);
    }

    @Override
    protected MessageSessionViewHolder createViewHolder() {
        return new MessageSessionViewHolder();
    }

    @Override
    protected void setItemData(int position, MessageSessionViewHolder viewHolder, View rootView) {
        // 更新时间
        final MessageSession item = getItem(position);
        viewHolder.mUserName.setText(item.user.name);
        viewHolder.mSessionContent.setText(item.lastChat.content);
        viewHolder.mUserIcon.setImageUrl(item.user.iconUrl);
        viewHolder.mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, UserInfoActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(Constants.TAG_USER, item.user);
//                mContext.startActivity(intent);
            }
        });

//        Date date = new Date(Long.parseLong(getItem(position).updateTime));
        viewHolder.mSessionTime.setText(TimeUtils.format(item.updateTime));
        int unReadCount = Integer.parseInt(item.unReadConut);
        unReadCount = unReadCount > 99 ? 99 : unReadCount;
        if(unReadCount > 0){
            viewHolder.mDot.setText(String.valueOf(unReadCount));
            viewHolder.mDot.setVisibility(View.VISIBLE);
        }else{
            viewHolder.mDot.setVisibility(View.GONE);
        }
    }
}
