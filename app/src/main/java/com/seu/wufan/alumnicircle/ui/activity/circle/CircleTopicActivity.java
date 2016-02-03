package com.seu.wufan.alumnicircle.ui.activity.circle;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.adapter.CircleTopicRecycleItemAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.widget.CustomLinearLayoutManager;
import com.seu.wufan.alumnicircle.ui.widget.swipeback.app.SwipeBackActivity;

/**
 * @author wufan
 * @date 2016/2/2
 */
public class CircleTopicActivity extends SwipeBackActivity {

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

        mTopicRecycleView.setLayoutManager(new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTopicRecycleView.setAdapter(new CircleTopicRecycleItemAdapter());
        mTopicRecycleView.setNestedScrollingEnabled(false);     // Disables scrolling for RecyclerView
        mTopicRecycleView.setHasFixedSize(false);

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