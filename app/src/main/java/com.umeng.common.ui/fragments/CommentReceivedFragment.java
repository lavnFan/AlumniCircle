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

package com.umeng.common.ui.fragments;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.widget.AbsListView;
import android.widget.ListView;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.imageloader.UMImageLoader;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.ReceivedCommentAdapter;
import com.umeng.common.ui.mvpview.MvpFeedView;
import com.umeng.common.ui.presenter.impl.CommentReceivedPresenter;
import com.umeng.common.ui.presenter.impl.FeedListPresenter;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;

/**
 * 我的消息中接到的评论Fragment
 */
public class CommentReceivedFragment extends BaseFragment<List<FeedItem>, FeedListPresenter>
        implements MvpFeedView {

    /**
     * ImageLoader
     */
    protected UMImageLoader mImageLoader = ImageLoaderManager.getInstance().getCurrentSDK();

    private RefreshLvLayout mRefreshLayout;
    protected ListView mListView;

    private ReceivedCommentAdapter mAdapter;

    protected Class mUserInfoClass;
    protected Class mTopicDetailClassName;
    protected Class mFeedDetailClassName;

    private BaseView mBaseView;

    @Override
    protected FeedListPresenter createPresenters() {
        return new CommentReceivedPresenter(this);
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_received_comment_layout");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        initRefreshView();
        initListView();
        mAdapter = createListViewAdapter();
        mRefreshLayout.setAdapter(mAdapter);
        mBaseView = (BaseView) mRootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_feed"));
    }

    private void initListView() {
        int feedListViewResId = ResFinder.getId("umeng_comm_listview");
        mListView = mRefreshLayout.findRefreshViewById(feedListViewResId);
        // 关闭动画缓存
        mListView.setAnimationCacheEnabled(false);
        // 开启smooth scrool bar
        mListView.setSmoothScrollbarEnabled(true);
    }

    protected ReceivedCommentAdapter createListViewAdapter() {
        ReceivedCommentAdapter adapter = new ReceivedCommentAdapter(getActivity(), true);
        adapter.setUserInfoClassName(mUserInfoClass);
        adapter.setTopicDetailClassName(mTopicDetailClassName);
        adapter.setFeedDetailClassName(mFeedDetailClassName);
        return adapter;
    }

    /**
     * view interface
     */
    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearListView() {
        mAdapter.getDataSource().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public List<FeedItem> getBindDataSource() {
        return mAdapter.getDataSource();
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
        if (mBaseView != null) {
            if (mAdapter.isEmpty()) {
                mBaseView.showEmptyView();
            } else {
                mBaseView.hideEmptyView();
            }
        }
    }

    private void initRefreshView() {
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

        // 添加footer
        mRefreshLayout.setDefaultFooterView();

        mRefreshLayout.setRefreshing(true); // important
    }

    /**
     * 加载更多数据</br>
     */
    protected void loadMoreFeed() {
        if (!DeviceUtils.isNetworkAvailable(getActivity())) {
            mRefreshLayout.setLoading(false);
            return;
        }
        mPresenter.fetchNextPageData();
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
    public void setIsVisitBtn(boolean isVisit) {

    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
