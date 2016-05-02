package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.mvp.presenter.login.LoginIPresenter;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.mvp.views.activity.ILoginView;
import com.seu.wufan.alumnicircle.ui.dialog.ProgressDialog;

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
    LoginIPresenter loginPresenter;
    ProgressDialog dialog;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void prepareDatas() {
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

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick(R.id.register_linear_layout)
    void register() {
        readyGo(RegisterActivity.class);
    }

    @OnClick(R.id.login_btn)
    void login() {
        if (loginPresenter.isValid(mPhoneNumEt.getText().toString(), mPasswordEt.getText().toString())) {
            dialog = new ProgressDialog(this);
            dialog.setContent("正在登录");
            dialog.show();
            loginPresenter.doLogin(mPhoneNumEt.getText().toString(), mPasswordEt.getText().toString());
        }
    }

    @Override
    public void loginSuccess() {
        if (dialog != null) {
            dialog.dismiss();
        }
        readyGoThenKill(MainActivity.class);
    }

    @Override
    public void loginFailed() {
        if (dialog != null) {
            dialog.dismiss();
        }
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
