package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.mvp.presenter.login.LoginIPresenter;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.mvp.views.activity.ILoginView;
import com.seu.wufan.alumnicircle.ui.dialog.ProgressDialog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

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

    UMShareAPI mShareAPI;

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
        readyGoThenKill(RegisterActivity.class);
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

    @OnClick(R.id.login_weixin_ll)
    void weixinLogin() {
        dialog = new ProgressDialog(this);
        dialog.setContent("正在授权");
        dialog.show();
        mShareAPI = UMShareAPI.get(this);
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        mShareAPI.doOauthVerify(this, platform, umAuthListener);
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
    public void goRegister(String user_id) {
        if (dialog != null) {
            dialog.dismiss();
        }
        Bundle bundle = new Bundle();
        bundle.putString(RegisterActivity.EXTRA_WEIXIN, "weixin");
        bundle.putString(RegisterActivity.EXTRA_USER_ID, user_id);
        readyGoThenKill(RegisterActivity.class, bundle);
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

    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if(i==UMAuthListener.ACTION_AUTHORIZE && map!=null){
                TLog.i("TAG", map.get("access_token") + " " + map.get("openid"));
                loginPresenter.doWeiXinLogin(map.get("access_token"), map.get("openid"));
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
