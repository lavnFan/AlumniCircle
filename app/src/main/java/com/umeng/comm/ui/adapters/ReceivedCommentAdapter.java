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

package com.umeng.comm.ui.adapters;

import android.content.Context;
import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.ui.adapters.viewholders.ReceivedCommentViewHolder;
import com.umeng.common.ui.adapters.CommonAdapter;


/**
 * 评论我的ListView Adapter
 */
public class ReceivedCommentAdapter extends CommonAdapter<FeedItem, ReceivedCommentViewHolder> {

    public boolean showReplyBtn = false;

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
//        viewHolder.mFeedTextTv.setOnClickListener(null);
        viewHolder.showFeedItem(getItem(position), showReplyBtn);
        if (!showReplyBtn) {
            return;
        }
//        viewHolder.mShareBtn.setCompoundDrawablePadding(10);
//        viewHolder.mShareBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
//                ResFinder.getDrawable("umeng_comm_reply"));
//        viewHolder.mShareBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                FeedItem feedItem = restoreFeedItem(getItem(position).sourceFeed);
//                if (feedItem.status >= FeedItem.STATUS_SPAM&&feedItem.status != FeedItem.STATUS_LOCK) {
//                    ToastMsg.showShortMsgByResName("umeng_comm_invalid_feed");
//                    return;
//                }
//                Intent intent = new Intent(mContext, FeedDetailActivity.class);
//                intent.setExtrasClassLoader(ImageItem.class.getClassLoader());
//                intent.putExtra(Constants.FEED, feedItem);
//                String commentId = feedItem.extraData.getString(HttpProtocol.COMMENT_ID_KEY);
//                // 传递评论的id
//                intent.putExtra(HttpProtocol.COMMENT_ID_KEY, commentId);
//                mContext.startActivity(intent);
//            }
//        });
    }

    /**
     * 获取原始的Feed内容,被@的消息中含有creator的用户名
     * 
     * @param originFeedItem
     * @return
     */
    private FeedItem restoreFeedItem(FeedItem originFeedItem) {
        FeedItem feedItem = originFeedItem.clone();
        feedItem.text = feedItem.text.split(":")[1];
        return feedItem;
    }

    protected String stringToNameTv() {
        return "";
    }

}
