package com.seu.wufan.alumnicircle.ui.fragment.circle;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.item.DynamicAgreeItem;
import com.seu.wufan.alumnicircle.ui.adapter.CircleDynamicAgreeItemAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.fragment.base.BaseLazyFragment;
import com.seu.wufan.alumnicircle.ui.view.ScrollLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class DynamicTextItemAgreeFragment extends BaseLazyFragment{

    @Bind(R.id.circle_dynamic_text_agree_lm_lv)
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
    protected void initViewsAndEvents() {
        initDatas();
        mLoadMoreLv.setFocusable(false);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_circle_dynamic_text_agree;
    }

    private void initDatas() {
        List<DynamicAgreeItem> entities = new ArrayList<DynamicAgreeItem>();
        for (int i = 0; i < 10; i++) {
            entities.add(new DynamicAgreeItem());
        }
        mAdapter = new CircleDynamicAgreeItemAdapter(getActivity());
        mAdapter.setmEntities(entities);
        mLoadMoreLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

}
