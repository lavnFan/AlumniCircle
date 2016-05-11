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
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.common.ui.adapters.viewholders.RecommendTopicViewHolder;
import com.umeng.common.ui.listener.TopicToTopicDetail;


/**
 * 推荐话题的Adapter
 */
public class RecommendTopicAdapter extends BackupAdapter<Topic, RecommendTopicViewHolder> {

    private String mFeedsStr = "";
    private String mFansStr = "";
    protected static final String DIVIDER = " / ";

    protected FollowListener<Topic> mListener;
    protected boolean isFromFindPage = false;// 是否来自于发现页面。对于来自发现页面需要单独处理，

    protected int mTopicColor = 0;
    protected int mTopicIcon = 0;
    protected TopicToTopicDetail ttt;
    /**
     * 推荐话题的显示样式跟推荐用户的样式相同
     * 
     * @param context
     */
    public RecommendTopicAdapter(Context context) {
        super(context);
        mTopicColor = ResFinder.getColor("umeng_comm_text_topic_light_color");
        mFeedsStr = ResFinder.getString("umeng_comm_feeds_num");
        mFansStr = ResFinder.getString("umeng_comm_fans_num");
        mTopicColor = ResFinder.getColor("umeng_topic_title");
        mTopicIcon = ResFinder.getResourceId(ResType.DRAWABLE,
                "umeng_comm_topic_icon");
    }

    public void setTtt(TopicToTopicDetail ttt) {
        this.ttt = ttt;
    }

    @Override
    protected RecommendTopicViewHolder createViewHolder() {
        return new RecommendTopicViewHolder();
    }

    @Override
    protected void setItemData(int position, final RecommendTopicViewHolder viewHolder, View rootView) {
        final Topic topic = getItem(position);
        viewHolder.mUserNameTextView.setText(topic.name);
        viewHolder.mUserNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewHolder.mUserNameTextView.setTextColor(ResFinder.getColor("umeng_comm_title"));

        if (TextUtils.isEmpty(topic.icon)||topic.icon.equals("null")){
            viewHolder.mImageView.setImageResource(mTopicIcon);
        }else {
            viewHolder.mImageView.setImageUrl(topic.icon, ImgDisplayOption.getTopicIconOption());
        }

//        viewHolder.mGenderImageView.setVisibility(View.GONE);
        viewHolder.mMsgFansTextView.setText(buildMsgFansStr(topic));

        setToggleButtonStatusAndEvent(viewHolder, topic);

//        if (isFromFindPage) {
            viewHolder.mUserNameTextView.setTextColor(ResFinder.getColor("umeng_comm_title"));
            rootView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    gotoTopicDetailPage(topic);
                }
            });
        }
        // 设置padding
        // setupItemViewPadding(viewHolder);
//    }

    // protected void setupItemViewPadding(ActiveUserViewHolder viewHolder) {
    // int left = viewHolder.mView.getLeft() + DeviceUtils.dp2px(mContext, 4);
    // int top = viewHolder.mView.getLeft();
    // int right = viewHolder.mView.getLeft();
    // int bottom = viewHolder.mView.getLeft();
    // viewHolder.mView.setPadding(left, top, right, bottom);
    // }

    /**
     * 构建feed数量、粉丝数量的字符串
     * 
     * @param topic
     * @return
     */
    protected String buildMsgFansStr(Topic topic) {
        StringBuilder builder = new StringBuilder(mFeedsStr);
        int feedCount = (int)topic.feedCount;
        String feedCountStr = CommonUtils.getLimitedCount(feedCount);
        builder.append(feedCountStr);

        builder.append(DIVIDER).append(mFansStr);

        int fansCount = (int)topic.fansCount;
        String fansCountStr = CommonUtils.getLimitedCount(fansCount);
        builder.append(fansCountStr);
        return builder.toString();
    }

    /**
     * 跳转到话题详情页面</br>
     * 
     * @param topic
     */
    //// TODO: 16/1/19 反射跳转到不同界面
    protected void gotoTopicDetailPage(Topic topic) {
        if (ttt!=null) {
            ttt.gotoTopicDetail(topic);
        }
//        Intent intent = new Intent();
//        ComponentName componentName = new ComponentName(mContext, TopicDetailActivity.class);
//        intent.setComponent(componentName);
//        intent.putExtra(Constants.TAG_TOPIC, topic);
//        ((Activity) mContext).startActivity(intent);
    }

    public void setFollowListener(FollowListener<Topic> listener) {
        this.mListener = listener;
    }

    /**
     * 设置是否来自于发送页面</br>
     * 
     * @param fromFind
     */
    public void setFromFindPage(boolean fromFind) {
        isFromFindPage = fromFind;
    }

    private void setToggleButtonStatusAndEvent(final RecommendTopicViewHolder viewHolder,
            final Topic topic) {
        viewHolder.mToggleButton.setChecked(topic.isFocused);
        viewHolder.mToggleButton.setOnClickListener(new Listeners.LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                viewHolder.mToggleButton.setClickable(true);
                mListener.onFollowOrUnFollow(topic, viewHolder.mToggleButton,
                        viewHolder.mToggleButton.isChecked());
            }
        });
    }

    public static interface FollowListener<T> {
        public void onFollowOrUnFollow(T t, ToggleButton toggleButton, boolean isFollow);
    }

}
