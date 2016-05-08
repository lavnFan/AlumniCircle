package com.seu.wufan.alumnicircle.ui.fragment.circle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.DynamicListRes;
import com.seu.wufan.alumnicircle.api.entity.TopicRes;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.circle.CirclePresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.ICircleView;
import com.seu.wufan.alumnicircle.ui.activity.circle.CircleTopicActivity;
import com.seu.wufan.alumnicircle.ui.activity.circle.DynamicTextActivity;
import com.seu.wufan.alumnicircle.ui.adapter.circle.DynamicItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class CircleFragment extends BaseLazyFragment implements ICircleView {

    @Bind(R.id.circle_top_topic_card_view)
    CardView mTopicCardView;
    @Bind(R.id.circle_dynamic_load_more_list_view)
    ScrollLoadMoreListView mDynamicLoadMoreLv;
    @Bind(R.id.circle_scroll_view)
    ScrollView mScrollView;
    @Bind(R.id.circle_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.circle_top_iv)
    ImageView mTopIv;
    @Bind(R.id.circle_top_topic_text_view)
    TextView mTopTopicTextView;
    @Bind(R.id.circle_top_data_tv)
    TextView mTopDataTv;
    @Bind(R.id.circle_top_people_tv)
    TextView mTopPeopleTv;

    private BasisAdapter mAdapter;
    private int page=1;

    @Inject
    CirclePresenter circlePresenter;

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void prepareData() {
        getApiComponent().inject(this);
        circlePresenter.attachView(this);
    }

    @Override
    protected void initViewsAndEvents() {
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onPullDown();
            }
        });
        mDynamicLoadMoreLv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                onPullUp();
            }
        });
        mDynamicLoadMoreLv.setFocusable(false);
        mDynamicLoadMoreLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TLog.i("INFO", "position" + position);
                readyGo(DynamicTextActivity.class);
            }
        });
        initDatas();
        mScrollView.smoothScrollTo(0, 0);

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_circle;
    }

    private void initDatas() {
        circlePresenter.getTopic();
        circlePresenter.getUserTimeDynamic(String.valueOf(page));
    }

    private void onPullUp() { //上拉刷新，加载更多
        mDynamicLoadMoreLv.setLoading(false);
    }

    private void onPullDown() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.circle_top_topic_card_view)
    void topTopicView() {
        readyGo(CircleTopicActivity.class);
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(getActivity());
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(getActivity());
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, getActivity());
    }

    @Override
    public void initTopic(TopicRes res) {
        CommonUtils.showImageWithGlide(getActivity(),mTopIv,res.getImage());
        mTopDataTv.setText(res.getTopic_date());
        mTopPeopleTv.setText(res.getTopic_id());
        String topicText = "#"+res.getTopic_text()+"#";
        mTopTopicTextView.setText(topicText);
    }

    @Override
    public void showDynamic(List<DynamicItem> listRes,boolean firstPage) {
        Log.i("TAG",firstPage+" :"+listRes.size());
        if(firstPage){
            mAdapter = new DynamicItemAdapter(getActivity());
            mAdapter.setmEntities(listRes);
            mDynamicLoadMoreLv.setAdapter(mAdapter);
        }else{
            mAdapter.addEntities(listRes);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        circlePresenter.destroy();
    }
}
