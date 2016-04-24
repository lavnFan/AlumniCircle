package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.mvp.presenter.impl.LoginPresenter;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.mvp.views.activity.ILoginView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/30
 */
public class LoginActivity extends BaseActivity implements ILoginView {
    @Bind(R.id.login_phone_num_et)
    EditText mPhoneNumEt;
    @Bind(R.id.login_password_et)
    EditText mPasswordEt;

    @Inject
    LoginPresenter loginPresenter;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void prepareData() {
        getApiComponent().inject(this);
        loginPresenter.attachView(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViewsAndEvents() {
        setToolbarBackHome(false);
    }

    @OnClick(R.id.register_linear_layout)
    void register() {
        readyGo(RegisterActivity.class);
    }

    @OnClick(R.id.login_btn)
    void login() {
        if (loginPresenter.isValid(mPhoneNumEt.getText().toString(), mPasswordEt.getText().toString())) {
            loginPresenter.doLogin(mPhoneNumEt.getText().toString(), mPasswordEt.getText().toString());
        }
    }

    @Override
    public String getUser_id() {
        return null;
    }

    @Override
    public void loginSuccess() {
        showToast("登录成功");
        readyGoThenKill(MainActivity.class);
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(this);
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(this);
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.destroy();
    }
}
