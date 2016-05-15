package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;

/**
 * @author wufan
 * @date 2016/5/14
 */
public class EducationActivity extends BaseSwipeActivity{

    public static final String EXTRA_EDU="edu";

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_education;
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
