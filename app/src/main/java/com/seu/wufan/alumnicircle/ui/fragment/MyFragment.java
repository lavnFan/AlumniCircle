package com.seu.wufan.alumnicircle.ui.fragment;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.fragment.base.BaseFragment;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class MyFragment extends BaseFragment{
    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void initViews(View view) {

    }

    @Override
    public void initDatas() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
