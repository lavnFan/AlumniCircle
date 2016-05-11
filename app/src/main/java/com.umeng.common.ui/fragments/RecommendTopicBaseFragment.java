package com.umeng.common.ui.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.BackupAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;
import com.umeng.common.ui.presenter.impl.RecommendTopicPresenter;
import com.umeng.common.ui.util.FontUtils;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;

/**
 * Created by wangfei on 16/1/19.
 */
public abstract class RecommendTopicBaseFragment extends BaseFragment<List<Topic>, RecommendTopicPresenter>
        implements View.OnClickListener, MvpRecommendTopicView {
    protected BackupAdapter<Topic, ?> mAdapter;
    protected ListView mTopicListView;
    protected RefreshLvLayout mRefreshLvLayout;
    protected boolean fromRecommedTopic = true;
    protected boolean mSaveButtonVisiable = true;
    protected DialogInterface.OnDismissListener mOnDismissListener;
    protected BaseView mBaseView;
    protected boolean isclick = true;
    protected Button nextButton;

    private boolean isVisit = true;

    @Override
    protected abstract int getFragmentLayout() ;

    @Override
    protected void initWidgets() {
        Log.d("init", "init widgets" + getClass().getSimpleName());
        FontUtils.changeTypeface(mRootView);
        initRefreshView(mRootView);
        initTitleView(mRootView);
        initAdapter();
    }

    @Override
    protected RecommendTopicPresenter createPresenters() {
        return new RecommendTopicPresenter(this, true);
    }

    protected void initTitleView(View rootView) {
        nextButton = (Button) rootView.findViewById(ResFinder.getId("umeng_comm_save_bt"));
        nextButton.setOnClickListener(this);
        nextButton.setText(ResFinder.getString("umeng_comm_skip"));
        nextButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        nextButton.setTextColor(ResFinder.getColor("umeng_comm_skip_text_color"));
        if (!mSaveButtonVisiable) {
            nextButton.setVisibility(View.GONE);
            rootView.findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(
                    this);
        } else {
            rootView.findViewById(ResFinder.getId("umeng_comm_setting_back")).setVisibility(
                    View.GONE);
        }
        TextView textView = (TextView) rootView.findViewById(ResFinder
                .getId("umeng_comm_setting_title"));
        textView.setText(ResFinder.getString("umeng_comm_recommend_topic"));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        rootView.findViewById(ResFinder.getId("umeng_comm_title_bar_root"))
                .setBackgroundColor(Color.WHITE);
    }

    /**
     * 设置保存按钮魏不可见。在设置页面显示推荐话题时，不需要显示</br>
     */
    public void setSaveButtonInVisiable() {
        // findViewById(ResFinder.getId("umeng_comm_save_bt")).setVisibility(View.INVISIBLE);
        mSaveButtonVisiable = false;
    }

    /**
     * 初始化刷新相关的view跟事件</br>
     *
     * @param rootView
     */
    protected void initRefreshView(View rootView) {
        int refreshResId = ResFinder.getId("umeng_comm_topic_refersh");

        Log.d("","id = "+refreshResId);
        mRefreshLvLayout = (RefreshLvLayout) rootView.findViewById(refreshResId);
        mRefreshLvLayout.setDefaultFooterView();
        // 推荐用户页面无加载更多跟下拉刷新
        if (fromRecommedTopic) {
            mRefreshLvLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    mPresenter.loadDataFromServer();
                }
            });
            mRefreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
                @Override
                public void onLoad() {
                    if(isCanLoadMore()){
                        mPresenter.loadMoreData();
                    }else{
                        onRefreshEnd();
                    }
                }
            });
        }

        int listViewResId = ResFinder.getId("umeng_comm_topic_listview");
        mTopicListView = mRefreshLvLayout.findRefreshViewById(listViewResId);
        if (!mSaveButtonVisiable) {
            // 目前推荐话题不需要刷新跟加载更多，因此暂时设置不可用
//            mRefreshLvLayout.setEnabled(false);
        } else {
            mRefreshLvLayout.setDefaultFooterView();
        }

        mBaseView = (BaseView) rootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_recommend_topic"));
    }

    protected void initAdapter() {
        RecommendTopicAdapter adapter = new RecommendTopicAdapter(getActivity());
        adapter.setFromFindPage(!mSaveButtonVisiable);
        mAdapter = adapter;
        adapter.setFollowListener(new RecommendTopicAdapter.FollowListener<Topic>() {

            @Override
            public void onFollowOrUnFollow(Topic topic, ToggleButton toggleButton,
                                           boolean isFollow) {
                if ( nextButton.getText().equals(ResFinder.getString("umeng_comm_skip"))){
                    nextButton.setText(ResFinder.getString("umeng_comm_next"));
                }
                if (isFollow) {
                    mPresenter.followTopic(topic, toggleButton);
                } else {
                    mPresenter.cancelFollowTopic(topic, toggleButton);
                }
            }
        });
        mTopicListView.setAdapter(mAdapter);
        setAdapterGotoDetail();
    }
    protected abstract void setAdapterGotoDetail();
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_save_bt")
                || id == ResFinder.getId("umeng_comm_setting_back")) {
            mOnDismissListener.onDismiss(null);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    @Override
    public List<Topic> getBindDataSource() {
        if (mAdapter!=null){
            return mAdapter.getDataSource();
        }else
            return null;
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshStart() {
        if(mRefreshLvLayout!=null) {
            mRefreshLvLayout.setRefreshing(true);
        }
    }

    @Override
    public void onRefreshEnd() {
        onRefreshEndNoOP();
        if (mAdapter!=null) {
            if (mAdapter.getCount() == 0) {
                mBaseView.showEmptyView();
            } else {
                mBaseView.hideEmptyView();
            }
        }
    }

    @Override
    public void onRefreshEndNoOP() {
        if (mRefreshLvLayout!=null){

            mRefreshLvLayout.setRefreshing(false);
            mRefreshLvLayout.setLoading(false);
        }
        if (mBaseView!=null) {
            mBaseView.hideEmptyView();
        }
    }

    @Override
    public void setIsVisitBtn(boolean isVisit) {
        if(!isAdded()){
            return;
        }
        this.isVisit = isVisit;
        if (!isVisit && !CommonUtils.isLogin(getActivity())) {
            mRefreshLvLayout.disposeLoginTipsView(true);
        } else {
            mRefreshLvLayout.disposeLoginTipsView(false);
        }
    }

    @Override
    public void onUserLogin() {
        if(!isVisit && CommonUtils.isLogin(getActivity())){
            isVisit = true;
            mRefreshLvLayout.disposeLoginTipsView(false);
        }
    }

    protected boolean isCanLoadMore(){
        boolean isLogin = (getActivity() != null) && CommonUtils.isLogin(getActivity());
        return isVisit || isLogin;
    }
}
