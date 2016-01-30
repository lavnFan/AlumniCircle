package com.seu.wufan.alumnicircle.ui.activity.login;

import android.os.Bundle;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.widget.swipeback.BackActivity;

/**
 * @author wufan
 * @date 2016/1/30
 */
public class LoginActivity extends BaseSwipeActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
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
