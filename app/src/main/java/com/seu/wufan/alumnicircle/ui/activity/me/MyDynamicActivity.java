package com.seu.wufan.alumnicircle.ui.activity.me;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyDynamicActivity  extends BaseSwipeActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_my_dynamic;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}