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

package com.umeng.comm.ui.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.listeners.Listeners.LoginOnViewClickListener;
import com.umeng.comm.core.listeners.Listeners.OnItemViewClickListener;
import com.umeng.comm.core.receiver.BaseBroadcastReceiver;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.ui.adapters.FeedCommentAdapter;
import com.umeng.comm.ui.adapters.FeedLikeAdapter;
import com.umeng.comm.ui.adapters.viewholders.FavouriteFeedItemViewHolder;
import com.umeng.comm.ui.presenter.impl.FeedDetailWBPresenter;
import com.umeng.common.ui.fragments.CommentEditFragment;
import com.umeng.common.ui.mvpview.MvpCommentView;
import com.umeng.common.ui.mvpview.MvpFeedDetailView;
import com.umeng.common.ui.mvpview.MvpLikeView;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.util.ViewFinder;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;


/**
 * 该类是某条Feed的详情页面,使用FeedItemViewParser解析单项的Feed数据.Feed相关的信息显示在评论列表的Header中,
 * 该页面会展示该条Feed的Like用户以及评论。
 */
public class FeedDetailFragment extends CommentEditFragment<List<FeedItem>, FeedDetailWBPresenter>
        implements MvpCommentView, MvpLikeView, MvpFeedDetailView {
    /**
     * 点击评论某项时的回调。用于在评论的EditView中显示回复XXX
     */
    private OnItemViewClickListener<String> mCommentClickListener;
    /**
     * 点击的评论的item view
     */
    protected View mClickItemView;
    /**
     * 当前点击的消息流的某一项
     */
    protected int mCurFeedItemIndex = 0;
    /**
     * 弹出评论EditVIew时，某项feed的滚动距离
     */
    protected int mScrollDis = 0;
    FavouriteFeedItemViewHolder mFeedViewHolder;
    // 由于开发者可能直接使用Fragment，在退出登录的时候，我们需要回到该Activity
    protected String mContainerClass = null;

//    TextView mLikeTextView;
    // 赞的用户列表
//    LikeView mLikeView;
    // 评论数量
//    TextView mCommentCountTextView;
//    // 转发数量
//    TextView mForwardCountTextView;
    /**
     * 页面下部的Like、评论、转发三个视图的根布局
     */
    View mActionsLayout;
    /**
     * LIKE视图布局
     */
    View mLikeActionLayout;
    /**
     * LIKE视图
     */
    TextView mLikeActionView;
    /**
     * 转发视图
     */
    View mForwardActionLayout;
    /**
     * 评论视图
     */
    View mCommentActionLayout;
    /**
     * 下拉刷新布局,评论ListView的parent
     */
    RefreshLvLayout mRefreshLayoutComment,mRefreshLayoutLike;
    /**
     * 评论ListView
     */
    private ListView mCommentListView,mLikeListView;
    /**
     * 评论ListView Adapter
     */
    FeedCommentAdapter mCommentAdapter;
    FeedLikeAdapter mLikeAdapter;
    Drawable drawableLine;
    private LoginReceiver mLoginReceiver;

    /**
     * 是否弹出评论编辑键盘
     */
    public Boolean mIsShowComment;
    public ViewPager viewPager;

    /**
     * 创建一个FeedDetailFragment对象，在feedDetailActivity使用
     *
     * @param feedItem
     * @return
     */
    public static FeedDetailFragment newFeedDetailFragment(FeedItem feedItem) {
        FeedDetailFragment fragment = new FeedDetailFragment();
        fragment.mFeedItem = feedItem;
        return fragment;
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_feed_detail_fragment");
    }

    @Override
    protected FeedDetailWBPresenter createPresenters() {
        super.createPresenters();
        return new FeedDetailWBPresenter(this, this, this, mFeedItem);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        View headerView = initFeedContentView();
        initRefreshLayout(headerView);
        initActionsView();
        BroadcastUtils.registerFeedBroadcast(getActivity(), mReceiver);
        mLoginReceiver = new LoginReceiver();
        getActivity().registerReceiver(mLoginReceiver, new IntentFilter(Constants.ACTION_LOGIN_SUCCESS));
    }

    @Override
    protected void setupOthers() {
        mContainerClass = getActivity().getClass().getName();
        // 填充数据
        setFeedItemData(mFeedItem);
    }

    public void updateFeedItem(FeedItem feedItem) {
        if (mFeedItem != null && feedItem != null) {
            mFeedItem.title = feedItem.title;
            mFeedItem.text = feedItem.text;
            mFeedItem.publishTime = feedItem.publishTime;
            mFeedItem.likeCount = feedItem.likeCount;
            mFeedItem.commentCount = feedItem.commentCount;
            mFeedItem.forwardCount = feedItem.forwardCount;
            mFeedItem.imageUrls = feedItem.imageUrls ;
            mFeedItem.category = feedItem.category;
            mFeedItem.isRecommended = feedItem.isRecommended;
            mFeedItem.isLiked = feedItem.isLiked;
        } else {
            mFeedItem = feedItem;
        }
        setFeedItemData(mFeedItem);
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isScroll = getArguments().getBoolean(Constants.TAG_IS_SCROLL, false);
        if (!isScroll) {
            return;
        }
        if (mIsShowComment){
            showCommentLayout();
        }
//        mCommentCountTextView.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                executeScroll();
//            }
//        }, 300);
    }

    /**
     * 执行滚动操作</br>
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void executeScroll() {
//        int scrollDis = mCommentCountTextView.getHeight() - mCommentCountTextView.getPaddingTop()
//                + 26;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mCommentListView.smoothScrollToPositionFromTop(1, scrollDis);
//        } else {
//            mCommentListView.smoothScrollToPosition(1);
//        }

    }

    /**
     * 加载Feed详情页面的Feed视图，该视图添加到评论列表的Header中
     *
     * @return
     */
    private View initFeedContentView() {
        drawableLine = ResFinder.getDrawable("blue_line_for_tv");
        drawableLine.setBounds(0,0,drawableLine.getMinimumWidth(), DeviceUtils.dp2px(getActivity(), 2));
        ViewFinder headerViewFinder = new ViewFinder(getActivity(),
                ResFinder.getLayout("umeng_comm_feed_detail_header"));

        // 显示Feed的Header View
        View headerView = headerViewFinder.findViewById(ResFinder
                .getId("umeng_comm_feed_content_layout"));
        // 构造FeedItemViewHolder用于对Feed进行控制
        mFeedViewHolder = new FavouriteFeedItemViewHolder(getActivity(), headerView);
        mFeedViewHolder.setIsFeedDetail();
        mFeedViewHolder.setShowFavouriteView(false);
        mFeedViewHolder.setContainerClass(mContainerClass);
        mFeedViewHolder.hideActionButtons();
//        mFeedViewHolder.setShareActivity(getActivity());
        mFeedViewHolder.setFeedItem(mFeedItem);
        setLikeCount(mFeedItem.likeCount);
        setForwardCount(mFeedItem.forwardCount);
        mFeedViewHolder.mForwardCountTextView.setClickable(false);
        setCommentCount(mFeedItem.commentCount);
        mFeedViewHolder.mCommentCountTextView.setBackgroundResource(ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_tab_bg"));
        mFeedViewHolder.mCommentCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRefreshLayoutComment.getVisibility() != View.VISIBLE) {
                    mRefreshLayoutComment.setVisibility(View.VISIBLE);
                    mRefreshLayoutLike.setVisibility(View.GONE);
                    mFeedViewHolder.mCommentCountTextView.setBackgroundResource(ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_tab_bg"));
                    mFeedViewHolder.mLikeCountTextView.setBackgroundColor(ResFinder.getColor("umeng_comm_white_color"));
                }
            }
        });
        mFeedViewHolder.mLikeCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRefreshLayoutLike.getVisibility()!=View.VISIBLE){
                    mRefreshLayoutLike.setVisibility(View.VISIBLE);
                    mRefreshLayoutComment.setVisibility(View.GONE);
                    mFeedViewHolder.mLikeCountTextView.setBackgroundResource(ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_tab_bg"));
                    mFeedViewHolder.mCommentCountTextView.setBackgroundColor(ResFinder.getColor("umeng_comm_white_color"));
                }
            }
        });
//        mFeedViewHolder.mForwardCountTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mPresenter.gotoForwardActivity(mFeedItem);
//            }
//        });
        // Like数量TextView
//        mLikeTextView = headerViewFinder.findViewById(ResFinder.getId("umeng_comm_like_count_tv"));
//
//        mLikeView = headerViewFinder.findViewById(ResFinder
//                .getId("umeng_comm_like_users_layout"));
//
//        mLikeView.setLikeonClickListener(new LikeonClickListener() {
//            @Override
//            public void onClickListener(CommUser user) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), UserInfoActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(Constants.TAG_USER, user);
//                getActivity().startActivity(intent);
//            }
//        });
//        mCommentCountTextView = headerViewFinder.findViewById(ResFinder
//                .getId("umeng_comm_comment_count_tv"));
//        mForwardCountTextView = headerViewFinder.findViewById(ResFinder
//                .getId("umeng_comm_forward_count_tv"));

        return headerViewFinder.getRootView();
    }

    private void initRefreshLayout(View headerView) {
        mRefreshLayoutComment = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_feed_refresh_layout"));
        mRefreshLayoutComment.setRefreshing(false);
        mRefreshLayoutComment.setEnabled(false);
        // 添加footer
        if (mFeedItem.commentCount > Constants.COUNT) {
            mRefreshLayoutComment.setDefaultFooterView();
        }
        mRefreshLayoutComment.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mRefreshLayoutComment.setRefreshing(false);
            }
        });
        mRefreshLayoutComment.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                mPresenter.loadMoreComments();
            }
        });
        mRefreshLayoutLike = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_feed_refresh_layout_like"));
        mRefreshLayoutLike.setRefreshing(false);
        mRefreshLayoutLike.setEnabled(false);
        // 添加footer
        if (mFeedItem.likeCount > Constants.COUNT) {
            mRefreshLayoutLike.setDefaultFooterView();
        }
        mRefreshLayoutLike.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mRefreshLayoutLike.setRefreshing(false);
            }
        });
        mRefreshLayoutLike.setVisibility(View.GONE);
        mRefreshLayoutLike.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                mPresenter.loadMoreLikes();
            }
        });
        headerView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCommentLayout.getVisibility() == View.VISIBLE) {
                    hideCommentLayout();
                    return true;
                }
                return false;
            }
        });
//        viewPager = mViewFinder.findViewById(ResFinder.getId("list_container"));
        mCommentListView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_comments_list"));
        mCommentListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCommentLayout.getVisibility() == View.VISIBLE) {
                    hideCommentLayout();
                    return true;
                }
                return false;
            }
        });

        mCommentListView.setOnItemClickListener(new Listeners.OnItemClickLoginListener() {

            @Override
            protected void doAfterLogin(View v, int position) {
                if (position == 0) {
                    return;
                }
                final int realPosition = position - 1;
                replayComment(realPosition);
            }
        });
        mLikeListView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_like_list"));
        mLikeListView.setVisibility(View.VISIBLE);
        // 添加header
        mLikeListView.addHeaderView(headerView);
        mCommentListView.addHeaderView(headerView);
    }

    private void replayComment(int position) {
        Comment comment = mCommentAdapter.getItem(position);
        mCommentPresenter.clickCommentItem(position, comment);
    }

    private void showCommentLayoutWitdCommentId() {
        // 从消息通知的评论页面进来该页面时需要弹出评论框
        if (mFeedItem.extraData.containsKey(HttpProtocol.COMMENT_ID_KEY)) {
            String commentId = mFeedItem.extraData.getString(HttpProtocol.COMMENT_ID_KEY);
            if (TextUtils.isEmpty(commentId)) {
                return;
            }
            int position = getCommentPosition(commentId);
            if (position >= 0) {
                mFeedItem.extraData.remove(HttpProtocol.COMMENT_ID_KEY);
                final Comment item = mCommentAdapter.getItem(position);
//                if (item.creator.id.equals(CommConfig.getConfig().loginedUser.id)) {
//                    ToastMsg.showShortMsgByResName("umeng_comm_do_not_reply_yourself");
//                } else {
                    showCommentLayout(position, item);
//                }
            }
        }
    }

    private int getCommentPosition(String commentId) {
        List<Comment> feedComments = mFeedItem.comments;
        for (int i = 0; i < feedComments.size(); i++) {
            final Comment item = feedComments.get(i);
            if (commentId.equals(item.id)) {
                return i;
            }
        }
        return -1;
    }

    private void initActionsView() {
        mActionsLayout = mViewFinder.findViewById(ResFinder.getId("umeng_comm_action_layout"));
        // 操作区域
        mLikeActionLayout = mViewFinder.findViewById(ResFinder.getId("umeng_comm_like_layout"));
        mLikeActionLayout.setOnClickListener(new LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                mPresenter.setFeedItem(mFeedItem);
                if (mFeedItem.isLiked) {
                    mPresenter.postUnlike(mFeedItem.id);
                } else {
                    mPresenter.postLike(mFeedItem.id);
                }
            }
        });

        // 赞的TextView
        mLikeActionView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_like_action_tv"));
        setupLikeView(mFeedItem.isLiked);

        mForwardActionLayout = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_forward_layout"));
        mForwardActionLayout.setOnClickListener(new LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                mPresenter.gotoForwardActivity(mFeedItem);
            }
        });
        mCommentActionLayout = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_comment_layout"));
        mCommentActionLayout.setOnClickListener(new LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                showCommentLayout();
            }
        });
    }

    private void setupLikeView(boolean isLiked) {
        if (isLiked) {
            mLikeActionView.setCompoundDrawablesWithIntrinsicBounds(
                    ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_like_p"), 0, 0,
                    0);
            mLikeActionView.setTextColor(ResFinder.getColor("umeng_comm_title"));
        } else {
            mLikeActionView.setCompoundDrawablesWithIntrinsicBounds(
                    ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_like_n"), 0, 0,
                    0);
            mLikeActionView.setTextColor(ResFinder.getColor("umeng_comm_color_a5"));
        }
    }

//    @Override
//    protected void postComment(String text) {
////        mPresenter.postComment(text, mReplyUser, mReplyCommentId);
//    }

    @Override
    public void showCommentLayout(int realPosition, Comment comment) {
        String hintString = getReplyPrefix(comment.creator);
        mReplyUser = comment.creator;
        mReplyCommentId = comment.id;

        showCommentLayout();

        mCommentEditText.setHint(hintString);
        mCommentListView.smoothScrollToPosition(realPosition);
    }

    protected void showCommentLayout() {
        super.showCommentLayout();
        mCommentEditText.setHint("");
        mActionsLayout.setVisibility(View.GONE);
        View titleView = getActivity().findViewById(ResFinder.getId("umeng_comm_feed_title_layout"));
        if(titleView != null){
            titleView.setVisibility(View.GONE);
        }
    }

    protected void hideCommentLayout() {
        super.hideCommentLayout();
        mActionsLayout.setVisibility(View.VISIBLE);
        View titleView = getActivity().findViewById(ResFinder.getId("umeng_comm_feed_title_layout"));
        if(titleView != null){
            titleView.setVisibility(View.VISIBLE);
        }
    }

    private void initLikeUserLayout(final String nextUrl) {
//        if (mFeedItem.likes.size() > 0) {
//            mLikeView.setVisibility(View.VISIBLE);
//            mLikeView.setLikeMoreonClickListener(new LikeMoreonClickListener() {
//                @Override
//                public void onClickListener(ArrayList<CommUser> list) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), LikeUsersActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(Constants.TAG_COUNT, mFeedItem.likeCount);
//                    intent.putExtra(HttpProtocol.NAVIGATOR_KEY, nextUrl);
//                    intent.putExtra(HttpProtocol.FEED_ID_KEY, mFeedItem.id);
//                    intent.putParcelableArrayListExtra(Constants.TAG_USERS, list);
//                   getActivity().startActivity(intent);
//                }
//            });
//            mLikeView.addLikeUsers(mFeedItem, nextUrl);
//            setLikeCount(mFeedItem.likes.size());
//        } else {
//            mLikeView.setVisibility(View.GONE);
//        }
    }

    /**
     * 填充消息流ListView每项的数据
     *
     * @param feedItem
     *
     */
    private void setFeedItemData(final FeedItem feedItem) {
        if (TextUtils.isEmpty(feedItem.id)) {
            return;
        }
        // 设置Feed Content View
        mFeedViewHolder.setFeedItem(feedItem);

        setLikeCount(feedItem.likeCount);
        setCommentCount(feedItem.commentCount);
        setForwardCount(feedItem.forwardCount);
        setupLikeView(feedItem.isLiked);
        // 初始化评论列表
        initCommentListView(feedItem);
    }

    private void setCommentCount(int count) {
        StringBuffer str = new  StringBuffer(ResFinder.getString("umeng_comm_comment"));
        str.append(" ").append(CommonUtils.getLimitedCount(count));
        mFeedViewHolder.mCommentCountTextView.setText(str.toString());
    }

    private void setLikeCount(int count) {
        StringBuffer str = new  StringBuffer(ResFinder.getString("umeng_comm_click_like"));
        str.append(" ").append(CommonUtils.getLimitedCount(mFeedItem.likeCount));
        mFeedViewHolder.mLikeCountTextView.setText(str.toString());
    }

    private void setForwardCount(int count) {
        StringBuffer str = new  StringBuffer(ResFinder.getString("umeng_comm_forward"));
        str.append(" ").append(CommonUtils.getLimitedCount(count));
        mFeedViewHolder.mForwardCountTextView.setText(str);
    }

    private void initCommentListView(FeedItem item) {
        mCommentAdapter = new FeedCommentAdapter(getActivity());
        mCommentAdapter.addDatasOnly(item.comments);
        mCommentListView.setAdapter(mCommentAdapter);
        mLikeAdapter = new FeedLikeAdapter(getActivity());
        mLikeAdapter.addDatasOnly(item.likes);
        mLikeListView.setAdapter(mLikeAdapter);
    }

    /*
     * 显示输入评论内容的布局
     */
    protected void showCommentLayout(int pos, boolean fromClickBtn) {
        if (mCommentClickListener != null) {
            mCommentClickListener.onItemClick(0, "");
        }
    }

    /**
     * 获取显示在EditText中显示的评论文本。不如：回复XXX</br>
     *
     * @return
     */
    private String getReplyPrefix(CommUser replyUser) {
        if (replyUser == null) {
            return "";
        }
        String replyText = ResFinder.getString("umeng_comm_reply");
        String colon = ResFinder.getString("umeng_comm_colon");
        return replyText + replyUser.name + colon;
    }

    @Override
    public void postCommentSuccess(Comment comment, CommUser replyUser) {
        mReplyCommentId = "";
        mReplyUser = null;
        if (mCommentAdapter == null) {
            return;
        }
        comment.replyUser = replyUser;
        mCommentAdapter.addToFirst(comment);
        setCommentCount(mFeedItem.commentCount);
        mCommentEditText.setText("");
        mCommentEditText.setHint("");
    }

    @Override
    public void loadMoreComment(List<Comment> comments) {
        mRefreshLayoutComment.setLoading(false);
        mRefreshLayoutLike.setLoading(false);
        comments.removeAll(mCommentAdapter.getDataSource());
        mCommentAdapter.addData(comments);
        // 存储数据
        mFeedItem.comments.addAll(comments);
    }

    @Override
    public void loadMoreLike(List<Like> likes) {
        mRefreshLayoutComment.setLoading(false);
        mRefreshLayoutLike.setLoading(false);
        likes.removeAll(mCommentAdapter.getDataSource());
        mLikeAdapter.addData(likes);
        // 存储数据
        mFeedItem.likes.addAll(likes);
    }

    @Override
    public void onRefreshEnd() {
        mRefreshLayoutComment.setLoading(false);
        mRefreshLayoutComment.setRefreshing(false);
        mRefreshLayoutLike.setLoading(false);
        mRefreshLayoutLike.setRefreshing(false);
    }

    public void fetchLikesComplete(String nexturl) {
        // 赞的用户列表
        initLikeUserLayout(nexturl);
    }

    @Override
    public void fetchCommentsComplete() {
        mCommentAdapter.updateListViewData(mFeedItem.comments);
    }

    @Override
    public void showAllComment(boolean result) {

    }

    @Override
    public void showOwnerComment(boolean result) {

    }

    @Override
    public void like(String id, boolean isLiked) {
        setupLikeView(isLiked);
        if (isLiked) {
            mLikeActionView.setCompoundDrawablesWithIntrinsicBounds(
                    ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_like_p"), 0, 0,
                    0);
            mLikeActionView.setTextColor(ResFinder.getColor("umeng_comm_title"));
        } else {
            mLikeActionView.setCompoundDrawablesWithIntrinsicBounds(
                    ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_feed_detail_like_n"), 0, 0,
                    0);
            mLikeActionView.setTextColor(ResFinder.getColor("umeng_comm_color_a5"));
        }
    }

    @Override
    public void updateLikeView(String nextUrl) {
        initLikeUserLayout(nextUrl);
        setLikeCount(mFeedItem.likeCount);
        mLikeAdapter.updateListViewData(mFeedItem.likes);
    }

    @Override
    public void updateCommentView() {
        mCommentAdapter.updateListViewData(mFeedItem.comments);
        showCommentLayoutWitdCommentId();
    }

    @Override
    public void onCommentDeleted(Comment comment) {
        mCommentAdapter.removeItem(comment);
        setCommentCount(mFeedItem.commentCount);
        BroadcastUtils.sendFeedUpdateBroadcast(getActivity(), mFeedItem);
    }

    @Override
    public void onDestroy() {
        BroadcastUtils.unRegisterBroadcast(getActivity(), mReceiver);
        getActivity().unregisterReceiver(mLoginReceiver);
        super.onDestroy();
    }

    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveFeed(Intent intent) {
            FeedItem feedItem = getFeed(intent);
            if (feedItem == null) {
                return;
            }
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_POST == type) {
                updateForwarCount(feedItem, 1);
            } else if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_DELETE == type) {
                updateForwarCount(feedItem, -1);
            }
        }
    };

    private void updateForwarCount(FeedItem item, int count) {
        if (TextUtils.isEmpty(item.sourceFeedId)) {
            return;
        }
        mFeedItem.forwardCount = mFeedItem.forwardCount + count;
        setForwardCount(mFeedItem.forwardCount);
    }

    @Override
    public void onRefreshStart() {
    }

    private class LoginReceiver extends BaseBroadcastReceiver{
        @Override
        protected void onReceiveIntent(Context context, Intent intent) {
            if(Constants.ACTION_LOGIN_SUCCESS.equals(intent.getAction())){
//                mPresenter.loadCommentFromServer();
            }
        }
    }
}
