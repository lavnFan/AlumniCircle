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

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.SearchTopicActivity;
import com.umeng.comm.ui.activities.TopicDetailActivity;
import com.umeng.comm.ui.adapters.TopicAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.listener.TopicToTopicDetail;
import com.umeng.common.ui.presenter.impl.RecommendTopicPresenter;
import com.umeng.common.ui.presenter.impl.TopicFgPresenter;


/**
 * 主页的三个tab中的话题页面
 */
public class TopicFragment extends RecommendTopicFragment {

    protected View mSearchLayout;
    private EditText mSearchEdit;
    private boolean mIsBackup = false;
    private InputMethodManager mInputMan;

    private boolean isFirstCreate = true;

    protected Dialog mProcessDialog;

    public TopicFragment() {
        super();
    }

    public static TopicFragment newTopicFragment() {
        return new TopicFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_topic_search");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
//        initRefreshView(mRootView);
        initSearchView(mRootView);
        mProcessDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));
//        initTitleView(mRootView);
        mInputMan = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }



    @Override
    protected RecommendTopicPresenter createPresenters() {
        return new TopicFgPresenter(this);
    }

    @Override
    protected void initTitleView(View rootView) {

        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_search_header_view"), null);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CommonUtils.visitNum == 0){
                    if (CommonUtils.isLogin(getActivity())){
                        Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                            @Override
                            public void onStart() {
                                if (getActivity()!=null&&!getActivity().isFinishing()){
                                    mProcessDialog.show();
                                }
                            }

                            @Override
                            public void onComplete(int code, CommUser userInfo) {
                                if (getActivity()!=null&&!getActivity().isFinishing()){
                                    mProcessDialog.dismiss();
                                }
                                if (code == 0) {
                                    Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                                    getActivity().startActivity(intent);
                                }
                            }
                        });
                    }
                }else {
                    Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
        TextView  searchtv = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        searchtv.setText(ResFinder.getString("umeng_comm_search_topic"));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)searchtv.getLayoutParams();
        params.bottomMargin = CommonUtils.dip2px(getActivity(), 8);
        mTopicListView.addHeaderView(headerView);
        mSearchLayout = findViewById(ResFinder.getId("umeng_comm_topic_search_title_layout"));
//        ((TextView)findViewById(ResFinder.getId("umeng_comm_comment_send_button"))).setText("搜索话题");
        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.checkLoginAndFireCallback(getActivity(), new Listeners.SimpleFetchListener<LoginResponse>() {
                    @Override
                    public void onComplete(LoginResponse response) {
                        if (CommonUtils.isLogin(getActivity())) {
                            Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                            getActivity().startActivity(intent);
                        }
                    }
                });

            }
        });
        mSearchLayout.setVisibility(View.GONE);
    }
    @Override
    protected void setAdapterGotoDetail() {
        ((RecommendTopicAdapter)mAdapter).setTtt(new TopicToTopicDetail() {
            @Override
            public void gotoTopicDetail(Topic topic) {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(getActivity(), TopicDetailActivity.class);
                intent.setComponent(componentName);
                intent.putExtra(Constants.TAG_TOPIC, topic);
                getActivity().startActivity(intent);
            }
        });
    }
    @Override
    protected void initAdapter() {
        mAdapter = new TopicAdapter(getActivity());
        ((TopicAdapter) mAdapter).setFollowListener(new RecommendTopicAdapter.FollowListener<Topic>() {

            @Override
            public void onFollowOrUnFollow(Topic topic, ToggleButton toggleButton,
                    boolean isFollow) {
                mPresenter.checkLoginAndExecuteOp(topic, toggleButton, isFollow);
            }
        });
        mTopicListView.setAdapter(mAdapter);
        setAdapterGotoDetail();
    }

    /**
     * 初始化搜索话题View跟事件处理</br>
     * 
     * @param rootView
     */
    protected void initSearchView(View rootView) {
//        mSearchLayout = findViewById(ResFinder.getId("umeng_comm_topic_search_title_layout"));
//        int searchEditResId = ResFinder.getId("umeng_comm_topic_edittext");
//        mSearchEdit = findViewById(searchEditResId);
//        mSearchEdit.setOnKeyListener(new android.view.View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    ((TopicFgPresenter) mPresenter).executeSearch(mSearchEdit.getText()
//                            .toString().trim());
//                }
//                return false;
//            }
//        });
//
//        // 话题本地搜索
//        mSearchEdit.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if (!TextUtils.isEmpty(s)) {
//                    if (!mIsBackup) {
//                        mIsBackup = true;
//                        mAdapter.backupData();
//                    }
//                    // 如果keyword不为空，做本地搜索
//                    List<Topic> result = mPresenter.localSearchTopic(s.toString());
//                    mAdapter.updateListViewData(result);
//                } else {
//                    // 显示本地所有的话题
//                    mAdapter.restoreData();
//                    mIsBackup = false;
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (isFirstCreate) {
                isFirstCreate = false; // 隐藏且为第一次创建Fragment（初始化时isVisibleToUser为不可见）
            } else {
//                if (mInputMan!=null) {
//                    mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0); // 隐藏且是非第一次创建，则隐藏软键盘
//                }
            }
        }
    }

    @Override
    protected void initRefreshView(View rootView) {
        super.initRefreshView(rootView);
        mRefreshLvLayout.setProgressViewOffset(false, 60,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLvLayout.setRefreshing(true);
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }

    @Override
    public void onPause() {
//        mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mProcessDialog != null){
            mProcessDialog.dismiss();
        }
    }
}
