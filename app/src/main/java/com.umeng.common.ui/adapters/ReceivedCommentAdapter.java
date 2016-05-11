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

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.common.ui.adapters.viewholders.ReceivedCommentViewHolder;


/**
 * 评论我的ListView Adapter
 */
public class ReceivedCommentAdapter extends CommonAdapter<FeedItem, ReceivedCommentViewHolder> {

    public boolean showReplyBtn = false;

    private Class mUserInfoClass;
    private Class mTopicDetailClassName;
    private Class mFeedDetailClassName;

    public ReceivedCommentAdapter(Context context) {
        super(context);
    }

    public ReceivedCommentAdapter(Context context, boolean showReply) {
        super(context);
        showReplyBtn = showReply;
    }

    @Override
    protected ReceivedCommentViewHolder createViewHolder() {
        return new ReceivedCommentViewHolder();
    }

    @Override
    protected void setItemData(final int position, ReceivedCommentViewHolder viewHolder, View rootView) {
        viewHolder.setFeedDetailClassName(mFeedDetailClassName);
        viewHolder.setTopicDetailClassName(mTopicDetailClassName);
        viewHolder.setUserInfoClassName(mUserInfoClass);
        viewHolder.showFeedItem(getItem(position), showReplyBtn);
    }

    public void setUserInfoClassName(Class userInfoClassName) {
        this.mUserInfoClass = userInfoClassName;
    }

    public void setTopicDetailClassName(Class topicDetailClassName) {
        this.mTopicDetailClassName = topicDetailClassName;
    }

    public void setFeedDetailClassName(Class feedDetailClassName) {
        this.mFeedDetailClassName = feedDetailClassName;
    }
}
