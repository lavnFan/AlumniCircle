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
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.TimeUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.listener.FrinendClickSpanListener;
import com.umeng.common.ui.listener.TopicClickSpanListener;
import com.umeng.common.ui.util.FeedViewRender;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class ReceivedCommentViewHolder extends ViewHolder {
    private static final String M = "m";

    private ImageView mUserAvatar;
    private TextView mCommentUserName;
    private TextView mCommentTime;
    public TextView mCommentContent;

    private ImageView mFeedImage;
    private TextView mFeedUserName;
    private TextView mFeedContent;

    private TextView mFeedSimpleUserName;
    private TextView mFeedSimpleContent;

    private View mFeedInfoHolder;
    private View mFeedSimpleInfoHolder;

    private FeedItem mFeedItem;

    private View mReplyCommentBtn;

    private ArrayMap<String, CommUser> mSourceFeedCreators = new ArrayMap<String, CommUser>();

    private Class mUserInfoClass;
    private Class mTopicDetailClassName;
    private Class mFeedDetailClassName;

    public ReceivedCommentViewHolder() {
    }

    public ReceivedCommentViewHolder(Context context, View rootView) {
        mContext = context;
        itemView = rootView;
        initWidgets();
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

    @Override
    protected int getItemLayout() {
        return ResFinder.getLayout("umeng_comm_received_comment_item");
    }

    @Override
    protected void initWidgets() {
        inflateFromXML();
    }

    protected void inflateFromXML() {
        int userAvatarResId = ResFinder.getId("umeng_comm_user_avatar");
        int commentUserNameResId = ResFinder.getId("umeng_comm_user_name");
        int commentTimeResId = ResFinder.getId("umeng_comm_comment_time");
        int commentContentResId = ResFinder.getId("umeng_comm_comment_content");

        int feedImgResId = ResFinder.getId("umeng_comm_feed_img");
        int feedUserResId = ResFinder.getId("umeng_comm_feed_user_name");
        int feedContentResId = ResFinder.getId("umeng_comm_feed_content");

        int feedSimpleUserNameResId = ResFinder.getId("umeng_comm_feed_simple_user_name");
        int feedSimpleContentResId = ResFinder.getId("umeng_comm_feed_simple_content");

        int feedInfoHolderResId = ResFinder.getId("umeng_comm_feed_info");
        int feedSimpleInfoHolderResId = ResFinder.getId("umeng_comm_feed_simple_info");

        int replyCommentResId = ResFinder.getId("umeng_comm_reply_btn");

        mUserAvatar = findViewById(userAvatarResId);
        mCommentUserName = findViewById(commentUserNameResId);
        mCommentTime = findViewById(commentTimeResId);
        mCommentContent = findViewById(commentContentResId);

        mFeedImage = findViewById(feedImgResId);
        mFeedUserName = findViewById(feedUserResId);
        mFeedContent = findViewById(feedContentResId);

        mFeedSimpleContent = findViewById(feedSimpleContentResId);
        mFeedSimpleUserName = findViewById(feedSimpleUserNameResId);

        mFeedInfoHolder = findViewById(feedInfoHolderResId);
        mFeedSimpleInfoHolder = findViewById(feedSimpleInfoHolderResId);

        mReplyCommentBtn = findViewById(replyCommentResId);

        findViewById(ResFinder.getId("umeng_comm_origin_feed_holder")).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startFeedDetailActivity();
            }
        });
    }

    public void showFeedItem(FeedItem item, boolean canReplyComment) {
        mFeedItem = item;
        setBaseFeeditemInfo();
        setForwardItemData(item, canReplyComment);
    }

    /**
     * 设置转发的数据
     *
     * @param item 转发的feed item
     */
    private void setForwardItemData(final FeedItem item, boolean canReplyComment) {
        // @原始feed的创建者
//        atOriginFeedCreator(item.sourceFeed);
        // 大于等于2表示该feed已经被删除
        if ((item.sourceFeed.status >= FeedItem.STATUS_SPAM && item.status != FeedItem.STATUS_LOCK) || isDeleted(item.sourceFeed)) {
//            mForwardTextTv.setGravity(Gravity.CENTER);
//            mForwardTextTv.setText(ResFinder.getString("umeng_comm_feed_deleted"));
//            if (mImageGv != null) {
//                mImageGv.setVisibility(View.GONE);
//            }
            // TODO 以下逻辑需要确定后修改
            // 如果该feed是收藏，且转发feed、原feed都被删除，则不显示原feed的状态
//            if (item.status >= FeedItem.STATUS_SPAM && item.sourceFeed.status >= FeedItem.STATUS_SPAM && item.status != FeedItem.STATUS_LOCK) {
//
//                mForwardLayout.setVisibility(View.GONE);
//            } else {
//                // 删除被转发的feed
//                deleteInvalidateFeed(item.sourceFeed);
//            }

            mFeedInfoHolder.setVisibility(View.GONE);
            mFeedSimpleInfoHolder.setVisibility(View.VISIBLE);

            mFeedSimpleUserName.setVisibility(View.GONE);
            mFeedSimpleContent.setText(ResFinder.getString("umeng_comm_feed_deleted"));

        } else {
//            mForwardTextTv.setGravity(Gravity.LEFT | Gravity.CENTER);
            boolean hasImg = (mFeedItem.sourceFeed.imageUrls != null && !mFeedItem.sourceFeed.imageUrls.isEmpty());


            // show sourceFeed creator name
            CommUser sourceFeedCreatorClone;
            if (mSourceFeedCreators.containsKey(mFeedItem.sourceFeed.creator.id)) {
                sourceFeedCreatorClone = mSourceFeedCreators.get(mFeedItem.sourceFeed.creator.id);
            } else {
                sourceFeedCreatorClone = new CommUser();
                // @前缀
                final String atPrefix = "@";
                sourceFeedCreatorClone.name = atPrefix + mFeedItem.sourceFeed.creator.name;
                mSourceFeedCreators.put(mFeedItem.sourceFeed.creator.id, sourceFeedCreatorClone);
            }

            if (hasImg) {
                mFeedInfoHolder.setVisibility(View.VISIBLE);
                mFeedSimpleInfoHolder.setVisibility(View.GONE);
                mFeedUserName.setText(sourceFeedCreatorClone.name);
                ImgDisplayOption option = ImgDisplayOption.getCommonDisplayOption();
                ImageLoaderManager.getInstance().getCurrentSDK().displayImage(mFeedItem.sourceFeed.getImages().get(0).thumbnail, mFeedImage, option);
            } else {
                mFeedInfoHolder.setVisibility(View.GONE);
                mFeedSimpleInfoHolder.setVisibility(View.VISIBLE);
                mFeedSimpleUserName.setVisibility(View.VISIBLE);
                mFeedSimpleUserName.setText(sourceFeedCreatorClone.name);
            }

            List<CommUser> list = new LinkedList<CommUser>();
            list.add(sourceFeedCreatorClone);
            FeedViewRender.renderFriendText(mContext, hasImg ? mFeedUserName : mFeedSimpleUserName, list, new FrinendClickSpanListener() {
                @Override
                public void onClick(CommUser user) {
                    startUserInfoActivity(mFeedItem.sourceFeed.creator);
                }
            });


            // show sourceFeed 解析被转发的@和话题
            FeedViewRender.parseTopicsAndFriends(hasImg ? mFeedContent : mFeedSimpleContent, item.sourceFeed, new TopicClickSpanListener() {
                @Override
                public void onClick(Topic topic) {
                    startTopicDetailActivity(topic);
                }
            }, new FrinendClickSpanListener() {
                @Override
                public void onClick(CommUser user) {
                    startUserInfoActivity(user);
                }
            });

//            if (mImageGv != null) {
//                mImageGv.setVisibility(View.VISIBLE);
//            }
        }

        if (canReplyComment) {
            mReplyCommentBtn.setVisibility(View.VISIBLE);
            mReplyCommentBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    FeedItem feedItem = item.sourceFeed;
                    startReplyCommentActivity(feedItem);
                }
            });
        } else {
            mReplyCommentBtn.setVisibility(View.GONE);
        }
    }

//    /**
//     * 获取原始的Feed内容,被@的消息中含有creator的用户名
//     *
//     * @param originFeedItem
//     * @return
//     */
//    private FeedItem restoreFeedItem(FeedItem originFeedItem) {
//        FeedItem feedItem = originFeedItem.clone();
//        feedItem.text = feedItem.text.split(":")[1];
//        return feedItem;
//    }

//    /**
//     * @param feedItem
//     */
//    protected void deleteInvalidateFeed(FeedItem feedItem) {
//        DatabaseAPI.getInstance().getFeedDBAPI().deleteFeedFromDB(feedItem.id);
//    }

    /**
     * 被转发的原始feed的创建者在转发时会被@,因此将其名字设置到文本中,然后将其添加到@的好友中.
     *
     * @param feedItem
     */
    protected void atOriginFeedCreator(FeedItem feedItem) {
        String contextText = feedItem.text;
        // @前缀
        final String atPrefix = "@" + feedItem.creator.name + ": ";
        if (!contextText.contains(atPrefix)) {
            feedItem.text = atPrefix + contextText;
            feedItem.atFriends.add(feedItem.creator);
        }
    }

    /**
     * 判断该feed是否被删除，本地[目前暂时按照从方法判断]</br>
     *
     * @param item
     * @return
     */
    private boolean isDeleted(FeedItem item) {
        if (TextUtils.isEmpty(item.publishTime)) {
            return true;
        }
        return false;
    }

    /**
     * 设置feedItem的基本信息（头像，昵称，内容、位置）</br>
     */
    protected void setBaseFeeditemInfo() {
        setupUserIcon(mUserAvatar, mFeedItem.creator);
        // 昵称
        mCommentUserName.setText(mFeedItem.creator.name);

        // 更新时间
        Date date = new Date(Long.parseLong(mFeedItem.publishTime));
        mCommentTime.setText(TimeUtils.format(date));
        // feed的文本内容
        FeedViewRender.parseTopicsAndFriends(mCommentContent, mFeedItem, new TopicClickSpanListener() {
            @Override
            public void onClick(Topic topic) {
                startTopicDetailActivity(topic);
            }
        }, new FrinendClickSpanListener() {
            @Override
            public void onClick(CommUser user) {
                startUserInfoActivity(user);
            }
        });

//        // 内容为空时Text隐藏布局,这种情况出现在转发时没有文本的情况
//        if (TextUtils.isEmpty(mFeedItem.text)) {
//            mFeedTextTv.setVisibility(View.GONE);
//        } else {
//            mFeedTextTv.setVisibility(View.VISIBLE);
//        }
//        mFeedTextTv.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mPresenter.clickFeedItem();
//            }
//        });
//        if (mIsShowDistance) {
//            mDistanceTextView.setText(mFeedItem.distance + M);
//        }
    }

//    private void setUserIcon() {
//        UserTypeUtil.SetUserType(mContext,mFeedItem.creator,muserTypeContainer);
//    }

    /**
     * 设置用户头像
     *
     * @param userIconImageView 用户头像的SquareImageView
     * @param user              用户头像的url
     */
    private void setupUserIcon(final View userIconImageView, final CommUser user) {
        if (user == null || userIconImageView == null) {
            return;
        }

        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
//        userIconImageView.setImageUrl(user.iconUrl, option);
        ImageLoaderManager.getInstance().getCurrentSDK().displayImage(user.iconUrl, mUserAvatar, option);

        userIconImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转用户中心前检查是否登录
//                mPresenter.gotoUserInfoActivity(user, mContainerClzName);
                startUserInfoActivity(user);
            }
        });
    }

    private void startUserInfoActivity(CommUser user) {
        if (mUserInfoClass != null) {
            Intent intent = new Intent(mContext, mUserInfoClass);
            intent.putExtra(Constants.TAG_USER, user);
            mContext.startActivity(intent);
        }
    }

    private void startTopicDetailActivity(Topic topic) {
        if (mTopicDetailClassName != null) {
            Intent intent = new Intent(mContext, mTopicDetailClassName);
            intent.putExtra(Constants.TAG_TOPIC, topic);
            mContext.startActivity(intent);
        }
    }

    private void startFeedDetailActivity() {
        if (mFeedItem.sourceFeed != null
                && mFeedItem.sourceFeed.status >= FeedItem.STATUS_SPAM
                && mFeedItem.status != FeedItem.STATUS_LOCK) { // TODO status需要确定
            ToastMsg.showShortMsgByResName("umeng_comm_feed_deleted");
            return;
        }
        if (mFeedDetailClassName != null) {
            Intent intent = new Intent(mContext, mFeedDetailClassName);
            String commentId = mFeedItem.sourceFeed.extraData.getString(HttpProtocol.COMMENT_ID_KEY);
            mFeedItem.sourceFeed.extraData.clear();
            intent.putExtra(Constants.FEED, mFeedItem.sourceFeed);
            mContext.startActivity(intent);
            mFeedItem.sourceFeed.extraData.putString(HttpProtocol.COMMENT_ID_KEY, commentId);
        }
    }

    private void startReplyCommentActivity(FeedItem feedItem) {
        if (feedItem.status >= FeedItem.STATUS_SPAM && feedItem.status != FeedItem.STATUS_LOCK) {
            ToastMsg.showShortMsgByResName("umeng_comm_invalid_feed");
            return;
        }
        if (mFeedDetailClassName != null) {
            Intent intent = new Intent(mContext, mFeedDetailClassName);
            intent.setExtrasClassLoader(ImageItem.class.getClassLoader());
            intent.putExtra(Constants.FEED, feedItem);
            String commentId = feedItem.extraData.getString(HttpProtocol.COMMENT_ID_KEY);
            // 传递评论的id
            intent.putExtra(HttpProtocol.COMMENT_ID_KEY, commentId);
//            Intent intent = new Intent(getActivity(), FeedDetailActivity.class);
//            intent.putExtra(Constants.TAG_FEED, feedItem);
            intent.putExtra(Constants.TAG_IS_COMMENT, true);
            intent.putExtra(Constants.TAG_IS_SCROLL, true);
            mContext.startActivity(intent);
        }
    }
}
