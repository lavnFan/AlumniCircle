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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.umeng.comm.core.beans.MessageChat;
import com.umeng.comm.core.utils.TimeUtils;
import com.umeng.common.ui.adapters.viewholders.MessageChatViewHolder;


public class MessageChatAdapter extends CommonAdapter<MessageChat, MessageChatViewHolder> {

    public MessageChatAdapter(Context context) {
        super(context);
    }

    @Override
    protected MessageChatViewHolder createViewHolder() {
        return new MessageChatViewHolder();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void setItemData(int position, MessageChatViewHolder viewHolder, View rootView) {
        // 更新时间
        MessageChat item = getItem(position);

        if(item.isCurrentUser()){
            viewHolder.mUserChatMsg.setText(item.content);
            viewHolder.mUserIcon.setImageUrl(item.creator.iconUrl);
//            Date date = new Date(Long.parseLong(item.createTime));
            viewHolder.mUserChatTime.setText(TimeUtils.format(item.createTime));
            viewHolder.mUserLayout.setVisibility(View.VISIBLE);
            viewHolder.mOtherUserLayout.setVisibility(View.GONE);
        }else{
            viewHolder.mOtherUserChatMsg.setText(item.content);
//            Date date = new Date(Long.parseLong(item.createTime));
            viewHolder.mOtherUserIcon.setImageUrl(item.creator.iconUrl);
            viewHolder.mOtherUserChatTime.setText(TimeUtils.format(item.createTime));
            viewHolder.mUserLayout.setVisibility(View.GONE);
            viewHolder.mOtherUserLayout.setVisibility(View.VISIBLE);
        }
    }
}
