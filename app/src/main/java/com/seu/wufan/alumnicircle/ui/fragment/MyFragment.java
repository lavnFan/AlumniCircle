package com.seu.wufan.alumnicircle.ui.fragment;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.fragment.base.BaseFragment;
import com.seu.wufan.alumnicircle.ui.fragment.base.BaseLazyFragment;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class MyFragment extends BaseLazyFragment {
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

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_me;
    }
}
