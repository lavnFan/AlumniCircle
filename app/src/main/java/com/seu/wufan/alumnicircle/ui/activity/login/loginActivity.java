package com.seu.wufan.alumnicircle.ui.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.widget.swipeback.BackActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/30
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick(R.id.register_linear_layout)
    void register(){
        readyGoThenKill(RegisterActivity.class);
    }


}
