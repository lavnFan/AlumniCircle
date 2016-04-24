package com.seu.wufan.alumnicircle.ui.activity.me;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;

import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class SettingActivity  extends BaseSwipeActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
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

    @OnClick(R.id.my_setting_exit_rl)
    void exit(){

    }
}