package com.seu.wufan.alumnicircle.ui.activity.circle;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.adapter.circle.CircleTopicRecycleItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.CustomLinearLayoutManager;
import com.seu.wufan.alumnicircle.ui.widget.swipeback.app.SwipeBackActivity;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/2
 */
public class CircleTopicActivity extends SwipeBackActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView collapsingTv;
    LinearLayout topicLl;

    RecyclerView mTopicRecycleView;
    NestedScrollView mNestedSv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_topic);

        initToolbar();
        initViews();
        initData();
        initListeners();
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.circle_topic_tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mTopicRecycleView = (RecyclerView) findViewById(R.id.circle_topic_recycle_view);
        mNestedSv = (NestedScrollView) findViewById(R.id.cirlce_topic_nested_scroll_view);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.circle_topic_collapsing_toolbar_layout);
        collapsingTv = (TextView) findViewById(R.id.circle_topic_collapsing_text_view);
        topicLl = (LinearLayout) findViewById(R.id.circle_topic_ll);

        CustomLinearLayoutManager customLinearLayoutManager= new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTopicRecycleView.setLayoutManager(customLinearLayoutManager);
        mTopicRecycleView.setAdapter(new CircleTopicRecycleItemAdapter());
        mTopicRecycleView.setNestedScrollingEnabled(false);     // Disables scrolling for RecyclerView
        mTopicRecycleView.setHasFixedSize(false);

        mTopicRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if(firstVisibleItemPosition==0){
                    topicLl.setVisibility(View.GONE);
                    collapsingTv.setVisibility(View.VISIBLE);
//                }else{
//                    topicLl.setVisibility(View.VISIBLE);
//                    collapsingTv.setVisibility(View.GONE);
//                }
            }
        });
    }

    private void initData() {

    }

    private void initListeners() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}