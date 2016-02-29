package com.seu.wufan.alumnicircle.ui.activity.me;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.me.CollectionItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyCollectionActivity  extends BaseSwipeActivity {
    @Bind(R.id.me_my_collection_load_lv)
    LoadMoreListView mLisView;
    private BasisAdapter mAdapter;
    @Override
    protected int getContentView() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
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
        mAdapter = new CollectionItemAdapter(this);
        mAdapter.setmEntities(entities);
        mLisView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}