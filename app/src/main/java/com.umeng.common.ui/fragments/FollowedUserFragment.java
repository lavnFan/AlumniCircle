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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.FollowedUserAdapter;
import com.umeng.common.ui.mvpview.MvpFollowedUserView;
import com.umeng.common.ui.presenter.impl.FollowedUserFgPresenter;
import com.umeng.common.ui.util.ViewFinder;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshGvLayout;
import com.umeng.common.ui.widgets.RefreshLayout;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 已关注的用户的Fragment
 */
public class FollowedUserFragment extends BaseFragment<List<CommUser>, FollowedUserFgPresenter>
        implements
        MvpFollowedUserView {

    /**
     * 已关注好友的适配器
     */
    protected FollowedUserAdapter mAdapter;

    /**
     * 下拉刷新的View
     */
    protected RefreshGvLayout mRefreshGvLayout;
    /**
     * 显示已关注好友的GridView
     */
    protected ListView mListView;
    /**
     * 用户id。根据该uid获取该用户关注的好友信息
     */
    protected String mUserId;
    /**
     * 用于更新follow、fans条数的更新
     */
    protected OnResultListener mListener;
    /**
     * 是否是第一次更新数据
     */
    AtomicBoolean isFirstRefresh = new AtomicBoolean(false);

    protected BaseView mBaseView;

    protected String mTargetClassName;

    public static FollowedUserFragment newInstance(String uid) {
        FollowedUserFragment followedUserFragment = new FollowedUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID_KEY, uid);
        followedUserFragment.setArguments(bundle);
        followedUserFragment.mUserId = uid;
        return followedUserFragment;
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_commm_followed_user_layout");
    }

    @Override
    protected void initWidgets() {
        int refershResId = ResFinder.getId("umeng_comm_user_swipe_layout");
        int gvResId = ResFinder.getId("umeng_comm_user_gridview");
        mViewFinder = new ViewFinder(mRootView);
        mRefreshGvLayout = mViewFinder
                .findViewById(refershResId);
        // 设置颜色
        mRefreshGvLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.loadDataFromServer();
            }
        });
        mRefreshGvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                mPresenter.loadMoreData();
            }

        });

        mListView = mViewFinder.findViewById(gvResId);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 跳转到用户信息页面‰
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                CommonUtils.checkLoginAndFireCallback(getActivity(),
//                        new SimpleFetchListener<LoginResponse>() {
//
//                            @Override
//                            public void onComplete(LoginResponse response) {
//                                if (response.errCode != ErrorCode.NO_ERROR) {
//                                    ToastMsg.showShortMsgByResName("umeng_comm_login_failed");
//                                    return;
//                                }
//                                Intent userIntent = new Intent(getActivity(),
//                                        UserInfoActivity.class);
//                                userIntent.putExtra(Constants.TAG_USER,
//                                        mAdapter.getItem(position));
//                                startActivity(userIntent);
//                            }
//                        });
//                Intent userIntent = new Intent(getActivity(),
//                        UserInfoActivity.class);
//                userIntent.putExtra(Constants.TAG_USER,
//                        mAdapter.getItem(position));
//                startActivity(userIntent);
            }
        });

        Log.d(getTag(), "### 屏幕宽度 : " + Constants.SCREEN_WIDTH);
//        if (Constants.SCREEN_WIDTH > 800) {
//            mListView.setNumColumns(Constants.SCREEN_WIDTH / 200);
//        }
//
//        mListView.setHorizontalSpacing(20);
//        mListView.setVerticalSpacing(10);

        mAdapter = createAdapter();
        mListView.setAdapter(mAdapter);

        boolean isCurrentUser = false;
        if (CommConfig.getConfig().loginedUser != null) {
            if (CommConfig.getConfig().loginedUser.id.equals(mUserId)) {
                isCurrentUser = true;
            }
        }
        mAdapter.setIsCurrentUser(isCurrentUser);

        mBaseView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_followed_user"));
        
        if (mAnimationResultListener != null) {
            mRefreshGvLayout.setOnScrollDirectionListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    // 1:向上滑动且第一项显示 (up)
                    // 0:向下且大于第一项 (down)
                    int firstVisible = mListView.getFirstVisiblePosition();
                    if ((status == 1 && firstVisible >= 0)
                            || (status == 0 && firstVisible == 0)) {
                        mAnimationResultListener.onResult(status);
                    }
                }
            });
        }
    }

    protected FollowedUserAdapter createAdapter(){
        return new FollowedUserAdapter(getActivity());
    }

    @Override
    protected FollowedUserFgPresenter createPresenters() {
        String uid = getArguments().getString(Constants.USER_ID_KEY);
        return new FollowedUserFgPresenter(this, uid);
    }

    /**
     * 设置follow、fans回调</br>
     * 
     * @param listener
     */
    public void setOnResultListener(OnResultListener listener) {
        this.mListener = listener;
    }

    @Override
    public List<CommUser> getBindDataSource() {
        return mAdapter.getDataSource();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void executeCallback(int count) {
        if (mListener != null) {
            mListener.onResult(count);
        }
    }

    @Override
    public void onRefreshStart() {
        mRefreshGvLayout.setRefreshing(true);
    }

    @Override
    public void onRefreshEnd() {
        mRefreshGvLayout.setRefreshing(false);
        mRefreshGvLayout.setLoading(false);
        if (mAdapter.getCount() == 0) {
            mBaseView.showEmptyView();
        } else {
            mBaseView.hideEmptyView();
        }
    }

    protected OnResultListener mAnimationResultListener = null;

    public void setOnAnimationResultListener(OnResultListener listener) {
        this.mAnimationResultListener = listener;
    }
    
    public void executeScrollTop(){
        if ( mListView.getFirstVisiblePosition() > mListView.getCount() / 4 ) {
            mListView.smoothScrollToPosition(0);
        }
    }

    public void updateFollowedState(String uId, boolean followedState){
        List<CommUser> data = mAdapter.getDataSource();
        int count = data.size();
        for (int i = 0; i < count; i++){
            if(data.get(i).id.equals(uId)){
                CommUser user = data.get(i);
                user.isFollowed = followedState;
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setTargetClassName(String targetClassName){
        this.mTargetClassName = targetClassName;
    }
    
    
}
