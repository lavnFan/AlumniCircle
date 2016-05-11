package com.umeng.common.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.UMImageLoader;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.CommonAdapter;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.mvpview.MvpFeedView;
import com.umeng.common.ui.presenter.impl.FeedListPresenter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.util.Filter;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangfei on 16/1/18.
 */
public abstract class FeedListBaseFragment<P extends FeedListPresenter, T extends CommonAdapter> extends
        CommentEditFragment<List<FeedItem>, P> implements MvpFeedView {
    /**
     * ImageLoader
     */
    protected UMImageLoader mImageLoader = ImageLoaderManager.getInstance().getCurrentSDK();
    /**
     * 下拉刷新, 上拉加载的布局, 包裹了Feeds ListView
     */
    protected RefreshLvLayout mRefreshLayout;
    /**
     * feeds ListView
     */
    protected ListView mFeedsListView;
    /**
     * 消息流适配器
     */
    protected T mFeedLvAdapter;
    /**
     * title的文本TextView
     */
    protected TextView mTitleTextView;

    /**
     * ListView的footers
     */
    protected List<View> mFooterViews = new ArrayList<View>();

    /**
     * 过滤掉某些关键字的filter
     */
    protected Filter<FeedItem> mFeedFilter;

    /**
     * 布局改变时的回调。主要用于监测输入法是否已经打开，并做相关的逻辑处理（评论中某项的具体滚动距离）
     */
    protected ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    /**
     * 当前登录的用户
     */
    protected CommUser mUser = CommConfig.getConfig().loginedUser;
    /**
     * 发表feed的button
     */
    protected ImageView mPostBtn;

    protected ViewStub mDaysView;

    protected LinearLayout mLinearLayout;
    protected Dialog mProcessDialog;
    List<String> mTabTitls = new ArrayList<String>();

    private BaseView mBaseView;

    protected boolean isVisit = true;

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_commm_feeds_frgm_layout");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        // 初始化视图
        initViews();
        // 初始化Feed Adapter
        initAdapter();
        // 请求中的状态
        mRefreshLayout.setRefreshing(true);
        registerBroadcast();
        mProcessDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));

        mBaseView = (BaseView) mRootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        if (mBaseView != null) {
            mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_feed"));
            mBaseView.hideEmptyView();
        }
    }

    /**
     * 初始化feed流 页面显示相关View
     */
    protected void initViews() {
        // 初始化刷新相关View跟事件
        initRefreshView();
        mPostBtn = mViewFinder.findViewById(ResFinder.getId("umeng_comm_new_post_btn"));
        mLinearLayout = mViewFinder.findViewById(ResFinder.getId("umeng_comm_ll"));
    }

    /**
     * 初始化下拉刷新试图, listview
     */
    protected void initRefreshView() {
        // 下拉刷新, 上拉加载的布局
        mRefreshLayout = mViewFinder.findViewById(ResFinder.getId("umeng_comm_swipe_layout"));
        // 下拉刷新时执行的回调
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // 加载最新的feed
                mPresenter.loadDataFromServer();
            }
        });

        // 上拉加载更多
        mRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                loadMoreFeed();
            }
        });

        // 滚动监听器, 滚动停止时才加载图片
        mRefreshLayout.addOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mImageLoader.resume();
                } else {
                    mImageLoader.pause();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });

        int feedListViewResId = ResFinder.getId("umeng_comm_feed_listview");
        // feed列表 listview
        mFeedsListView = mRefreshLayout.findRefreshViewById(feedListViewResId);
        // 添加footer
        mRefreshLayout.setDefaultFooterView();
        // 关闭动画缓存
        mFeedsListView.setAnimationCacheEnabled(false);
        // 开启smooth scrool bar
        mFeedsListView.setSmoothScrollbarEnabled(true);


    }

    /**
     * 隐藏评论的布局跟软键盘</br>
     */
    public void hideCommentLayoutAndInputMethod() {
        resetCommentLayout();
        hideInputMethod();
        showPostButtonWithAnim();
    }

    /**
     * 关闭输入法</br>
     */
    @SuppressWarnings("deprecation")
    protected void hideInputMethod() {
        if (CommonUtils.isActivityAlive(getActivity())) {
            sendInputMethodMessage(Constants.INPUT_METHOD_DISAPPEAR, mCommentEditText);
            mRootView.getRootView().getViewTreeObserver().removeGlobalOnLayoutListener(
                    mOnGlobalLayoutListener);
        }
    }

    /**
     *
     */
    protected void showPostButtonWithAnim() {
    }

    protected abstract void deleteInvalidateFeed(FeedItem feedItem);

    protected abstract void updateAfterDelete(FeedItem feedItem);

    /**
     * 加载更多数据</br>
     */
    protected void loadMoreFeed() {
        // 没有网络的情况下从数据库加载
        if (!DeviceUtils.isNetworkAvailable(getActivity())) {
            mPresenter.loadDataFromDB();
            mRefreshLayout.setLoading(false);
            return;
        }
        if(isCanLoadMore()){
            mPresenter.fetchNextPageData();
        }else{
            onRefreshEnd();
        }
    }

    protected abstract T createListViewAdapter();

    /**
     * 初始化适配器
     */
    protected abstract void initAdapter();

    protected abstract void addOnGlobalLayoutListener(final int position);

    @Override
    public void onResume() {
        super.onResume();
        onBaseResumeDeal();
    }

    /**
     * 基本的 OnResume处理逻辑</br>
     */
    protected void onBaseResumeDeal() {
        mFeedsListView.postDelayed(new Runnable() {

            @Override
            public void run() {
                hideInputMethod();
                if (mImageLoader != null) {
                    // 启动加载数据
                    mImageLoader.resume();
                }
            }
        }, 300);
    }

    public void onStop() {
        resetCommentLayout();
        super.onStop();
    }

    protected boolean isMyPage(FeedItem feedItem) {
        return feedItem != null;
    }

    protected void onCancelFollowUser(CommUser user) {
        Log.d(getTag(), "### cancel follow user");
    }

    /**
     * 数据同步处理
     */
    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveUser(Intent intent) {
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            CommUser user = getUser(intent);
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_UPDATE) {// 更新用户信息
                updatedUserInfo(user);
                return;
            }
            if (!CommonUtils.isMyself(mUser)) {// 如果不是登录用户，则不remove feed
                return;
            }

            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_CANCEL_FOLLOW) {
                // 预留一个hook函数,当取消对某个用户的关注，移除主页上该用户的feed。其他页面不进行操作
                onCancelFollowUser(user);
            }
        }

        public void onReceiveFeed(Intent intent) {// 发送or删除时
            FeedItem feedItem = getFeed(intent);
            if (feedItem == null) {
                return;
            }
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_POST == type && isMyFeedList()) {
                postFeedComplete(feedItem);
            } else if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_DELETE == type) {
                deleteFeedComplete(feedItem);
            } else if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_FAVOURITE == type) {
                dealFavourite(feedItem);
            }

            mFeedsListView.invalidate();
        }

        // 更新Feed的相关数据。包括like、comment、forward数量修改
        public void onReceiveUpdateFeed(Intent intent) {
            if (mFeedLvAdapter != null) {

                FeedItem item = getFeed(intent);
                List<FeedItem> items = mFeedLvAdapter.getDataSource();
                for (FeedItem feed : items) {
                    if (feed.id.equals(item.id)) {
                        // feed = item;
                        feed.isLiked = item.isLiked;
                        feed.likeCount = item.likeCount;
                        feed.likes = item.likes;
                        feed.commentCount = item.commentCount;
                        feed.comments = item.comments;
                        feed.forwardCount = item.forwardCount;
                        feed.isCollected = item.isCollected;
                        feed.category = item.category;
                        break;
                    }
                }
                // 此处不可直接调用adapter.notifyDataSetChanged，其他地方在notifyDataSetChanged（）方法中又逻辑处理
                notifyDataSetChanged();
            }
        }
    };

    /**
     * 在触发收藏操作时，需要收藏feed同步</br>
     */
    protected void dealFavourite(FeedItem feedItem) {
    }

    public void updatedUserInfo(CommUser user) {
//        mUser = user;
//        List<FeedItem> feedItems = mFeedLvAdapter.getDataSource();
//        for (FeedItem feed : feedItems) {
//            updateFeedContent(feed, user);
//        }
//        mFeedLvAdapter.notifyDataSetChanged();
    }

    // TODO 此处对于invalidate需要重构
    protected void postFeedComplete(FeedItem feedItem) {
        // mFeedLvAdapter.addToFirst(feedItem);
        // 此时需要排序，确保置顶的feed放在最前面
        mFeedLvAdapter.getDataSource().add(feedItem);
        mPresenter.sortFeedItems(mFeedLvAdapter.getDataSource());
        mFeedLvAdapter.notifyDataSetChanged();
        mFeedsListView.setSelection(0);
        updateForwardCount(feedItem, 1);
    }

    protected void deleteFeedComplete(FeedItem feedItem) {
        mFeedLvAdapter.getDataSource().remove(feedItem);
        int len = mFeedLvAdapter.getDataSource().size();
        for (int i = 0; i < len; i++){
            FeedItem item = (FeedItem) mFeedLvAdapter.getDataSource().get(i);
            if(item.sourceFeed != null && item.sourceFeed.id.equals(feedItem.id)){
                item.sourceFeed.status = FeedItem.STATUS_DELETE;
            }
        }
        mFeedLvAdapter.notifyDataSetChanged();
        updateForwardCount(feedItem, -1);
        Log.d(getTag(), "### 删除feed");
    }

    protected boolean isMyFeedList() {
        return true;
    }

    /**
     * 更新转发数</br>
     *
     * @param item
     */
    protected void updateForwardCount(FeedItem item, int count) {
        if (TextUtils.isEmpty(item.sourceFeedId)) {
            return;
        }
        List<FeedItem> items = mFeedLvAdapter.getDataSource();
        for (FeedItem feedItem : items) {
            if (feedItem.id.equals(item.sourceFeedId)) {
                feedItem.forwardCount = feedItem.forwardCount + count;
                mFeedLvAdapter.notifyDataSetChanged();
                break;
            }
        }

    }

    /**
     * 判断该Feed是否来源于特定用户</br>
     *
     * @param feedItem
     * @return
     */
    protected boolean isMyFeed(FeedItem feedItem) {
        CommUser user = CommConfig.getConfig().loginedUser;
        if (user == null || TextUtils.isEmpty(user.id)) {
            return false;
        }
        return feedItem.creator.id.equals(user.id);
    }

    /**
     * 用户信息修改以后更新feed的用户信息
     *
     * @param user
     */


    protected void updateFeedContent(FeedItem feed, CommUser user) {
        if (isMyFeed(feed)) {
            feed.creator = user;
        }

        // 更新like的创建者信息
        updateLikeCreator(feed.likes, user);
        // 更新评论信息
        updateCommentCreator(feed.comments, user);
        // 更新at好友的creator
        updateAtFriendCreator(feed.atFriends, user);
        // 转发类型的feed
        if (feed.sourceFeed != null) {
            updateFeedContent(feed.sourceFeed, user);
        }
    }

    protected void updateLikeCreator(List<Like> likes, CommUser user) {
        for (Like likeItem : likes) {
            if (likeItem.creator.id.equals(user.id)) {
                likeItem.creator = user;
            }
        }
    }

    protected void updateCommentCreator(List<Comment> comments, CommUser user) {
        for (Comment commentItem : comments) {
            if (commentItem.creator.id.equals(user.id)) {
                commentItem.creator = user;
            }
        }
    }

    protected void updateAtFriendCreator(List<CommUser> friends, CommUser user) {
        for (CommUser item : friends) {
            if (item.id.equals(user.id)) {
                item = user;
            }
        }
    }

    @Override
    public void postCommentSuccess(Comment comment, CommUser replyUser) {
        super.postCommentSuccess(comment, replyUser);
        mFeedLvAdapter.notifyDataSetChanged();// 刷新ListVIew，更新评论数字
        mCommentEditText.setText(""); // 清除评论内容
    }

    /**
     * 设置feed的过滤器</br>
     *
     * @param filter
     */
    public void setFeedFilter(Filter<FeedItem> filter) {
        mFeedFilter = filter;
    }

    /**
     * 过滤数据</br>
     *
     * @return
     */
    protected List<FeedItem> filteFeeds(List<FeedItem> list) {
        List<FeedItem> destList = mFeedFilter != null ? mFeedFilter.doFilte(list) : list;
        // 移除status>=2的feed，具体值得的含义参考文档说明
        Iterator<FeedItem> iterator = destList.iterator();
        while (iterator.hasNext()) {
            FeedItem item = iterator.next();
            if (item.status > 1 && item.status != FeedItem.STATUS_LOCK && mFeedItem.status != FeedItem.STATUS_LOCK) {
                iterator.remove();
            }
        }
        return destList;
    }

    protected void resetCommentLayout() {
        if (mCommentLayout != null) {
            mCommentLayout.setVisibility(View.INVISIBLE);
        }
        if (mCommentEditText != null) {
            mCommentEditText.setText("");
        }
    }

    protected void registerBroadcast() {
        // 注册广播接收器
        BroadcastUtils.registerUserBroadcast(getActivity(), mReceiver);
        BroadcastUtils.registerFeedBroadcast(getActivity(), mReceiver);
        BroadcastUtils.registerFeedUpdateBroadcast(getActivity(), mReceiver);
    }

    @Override
    public void onDestroy() {
        BroadcastUtils.unRegisterBroadcast(getActivity(), mReceiver);
        super.onDestroy();
    }

    @Override
    public void clearListView() {
        if (mFeedLvAdapter != null) {
            mFeedLvAdapter.getDataSource().clear();
            mFeedLvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateCommentView() {
    }

    @Override
    public void onRefreshStart() {
        //the fragment has detached with activity
        if (!this.isAdded()) {
            return;
        }
        mRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefreshEnd() {
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setLoading(false);
        if (mBaseView != null && mFeedLvAdapter != null) {
            if (mFeedLvAdapter.isEmpty()) {
                mBaseView.showEmptyView();
            } else {
                mBaseView.hideEmptyView();
            }
        }
    }

    @Override
    public abstract List<FeedItem> getBindDataSource();

    @Override
    public abstract void notifyDataSetChanged();

    /**
     * 判断是否需要展示最热在推荐帖子界面
     *
     * @param isShow
     */
    protected void showHotView(boolean isShow) {
        if (mDaysView != null) {
            if (isShow) {
                mDaysView.setVisibility(View.VISIBLE);
            } else {
                mDaysView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void onUserLogin() {
        if(!isVisit && CommonUtils.isLogin(getActivity())){
            isVisit = true;
            mRefreshLayout.disposeLoginTipsView(false);
        }
    }

    @Override
    public void setIsVisitBtn(boolean isVisit) {
        //the fragment has detached with activity
        if (!this.isAdded()) {
            return;
        }
        this.isVisit = isVisit;
        if (!isVisit && !CommonUtils.isLogin(getActivity())) {
            mRefreshLayout.disposeLoginTipsView(true);
        } else {
            mRefreshLayout.disposeLoginTipsView(false);
        }
    }

    protected boolean isCanLoadMore(){
        boolean isLogin = (getActivity() != null) && CommonUtils.isLogin(getActivity());
        return isVisit || isLogin;
    }
}
