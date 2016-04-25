package com.seu.wufan.alumnicircle.ui.activity.me;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.SwipeBackBaseActivity;
import com.seu.wufan.alumnicircle.mvp.presenter.impl.SettingPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISettingView;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class SettingActivity extends SwipeBackBaseActivity implements ISettingView{

    @Inject
    SettingPresenter settingPresenter;

    @Override
    protected void prepareData() {
        getApiComponent().inject(this);
        settingPresenter.attachView(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @OnClick(R.id.my_setting_exit_rl)
    void exit(){
        settingPresenter.exit();
    }

    @Override
    public void showNetCantUse() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showToast(@NonNull String s) {

    }

    @Override
    public void exitToLogin() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingPresenter.destroy();
    }
}