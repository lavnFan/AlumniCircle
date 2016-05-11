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
import android.util.TypedValue;
import android.view.View;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.viewholders.RecommendTopicViewHolder;


/**
 * 地理位置和好友选择的Adapter基类
 */
public class SelectTopicAdapter extends CommonAdapter<Topic, RecommendTopicViewHolder> {

    protected String mFeedsStr = "";
    protected String mFansStr = "";
    protected final String DIVIDER = " / ";

    protected int mTopicColor = 0;
    protected int mTopicIcon = 0;

    /**
     * 推荐话题的显示样式跟推荐用户的样式相同
     *
     * @param context
     */
    public SelectTopicAdapter(Context context) {
        super(context);
        mTopicColor = ResFinder.getColor("umeng_comm_text_topic_light_color");
        mFeedsStr = ResFinder.getString("umeng_comm_feeds_num");
        mFansStr = ResFinder.getString("umeng_comm_fans_num");
        mTopicColor = ResFinder.getColor("umeng_topic_title");
        mTopicIcon = ResFinder.getResourceId(ResFinder.ResType.DRAWABLE, "umeng_comm_topic_icon");
    }

    @Override
    protected RecommendTopicViewHolder createViewHolder() {
        return new RecommendTopicViewHolder();
    }

    @Override
    protected void setItemData(int position, final RecommendTopicViewHolder viewHolder, View rootView) {
        final Topic topic = getItem(position);
        viewHolder.mUserNameTextView.setText(topic.name);
        viewHolder.mUserNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        viewHolder.mUserNameTextView.setTextColor(ResFinder.getColor("umeng_comm_title"));
        viewHolder.mImageView.setImageResource(mTopicIcon);
        viewHolder.mImageView.setImageUrl(topic.icon, ImgDisplayOption.getTopicIconOption());
        viewHolder.mMsgFansTextView.setText(buildMsgFansStr(topic));
        viewHolder.mUserNameTextView.setTextColor(ResFinder.getColor("umeng_comm_title"));
        viewHolder.mToggleButton.setVisibility(View.GONE);
    }

    /**
     * 构建feed数量、粉丝数量的字符串
     *
     * @param topic
     * @return
     */
    protected String buildMsgFansStr(Topic topic) {
        StringBuilder builder = new StringBuilder(mFeedsStr);
        builder.append(topic.feedCount);
        builder.append(DIVIDER).append(mFansStr);
        builder.append(topic.fansCount);
        return builder.toString();
    }
}

