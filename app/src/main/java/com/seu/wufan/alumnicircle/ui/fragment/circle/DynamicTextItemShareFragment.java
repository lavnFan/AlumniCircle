package com.seu.wufan.alumnicircle.ui.fragment.circle;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicShareItem;
import com.seu.wufan.alumnicircle.ui.adapter.circle.CircleDynamicShareItemAdapter;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class DynamicTextItemShareFragment extends BaseLazyFragment{

    @Bind(R.id.circle_dynamic_text_share_lm_lv)
    ScrollLoadMoreListView mLoadMoreLv;

    private BasisAdapter mAdapter;
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

    }

    @Override
    protected void initViewsAndEvents() {
        initDatas();
        mLoadMoreLv.setFocusable(false);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_circle_dynamic_text_share;
    }

    private void initDatas() {
        List<DynamicShareItem> entities = new ArrayList<DynamicShareItem>();
        for (int i = 0; i < 10; i++) {
            entities.add(new DynamicShareItem());
        }
        mAdapter = new CircleDynamicShareItemAdapter(getActivity());
        mAdapter.setmEntities(entities);
        mLoadMoreLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}