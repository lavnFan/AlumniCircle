package com.seu.wufan.alumnicircle.ui.activity.me;

import android.view.View;
import android.widget.ScrollView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.circle.DynamicItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyDynamicActivity  extends BaseSwipeActivity {

    @Bind(R.id.me_my_dynamic_load_lv)
    ScrollLoadMoreListView mLisView;
    @Bind(R.id.my_dynamic_scroll_view)
    ScrollView mScrollView;
    private BasisAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_dynamic;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        mScrollView.smoothScrollTo(0,0);
        initDatas();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    private void initDatas() {
        List<DynamicItem> entities = new ArrayList<DynamicItem>();
        for (int i = 0; i < 10; i++) {
            entities.add(new DynamicItem());
        }
        mAdapter = new DynamicItemAdapter(this);
        mAdapter.setmEntities(entities);
        mLisView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}