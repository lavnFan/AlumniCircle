package com.seu.wufan.alumnicircle.ui.fragment.circle;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.item.DynamicCommentItem;
import com.seu.wufan.alumnicircle.ui.adapter.CircleDynamicCommentItemAdapter;
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
public class DynamicTextItemCommentFragment extends BaseLazyFragment{

    @Bind(R.id.circle_dynamic_text_comment_lm_lv)
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

    }

    private void initDatas() {
        List<DynamicCommentItem> entities = new ArrayList<DynamicCommentItem>();
        for (int i = 0; i < 10; i++) {
            entities.add(new DynamicCommentItem());
        }
        mAdapter = new CircleDynamicCommentItemAdapter(getActivity());
        mAdapter.setmEntities(entities);
        mLoadMoreLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_circle_dynamic_text_comment;
    }
}
