package com.seu.wufan.alumnicircle.ui.activity;

import android.view.View;
import android.widget.Button;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends  BaseActivity{

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
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

    @OnClick(R.id.test)
    void click(){
        readyGo(LoginActivity.class);
    }
}
