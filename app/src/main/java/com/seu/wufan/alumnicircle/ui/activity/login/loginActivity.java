package com.seu.wufan.alumnicircle.ui.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.ApiManager;
import com.seu.wufan.alumnicircle.model.api.LoginReq;
import com.seu.wufan.alumnicircle.model.api.LoginRes;
import com.seu.wufan.alumnicircle.model.api.UserInfoRes;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.utils.CommonUtils;
import com.seu.wufan.alumnicircle.ui.utils.NetUtils;
import com.seu.wufan.alumnicircle.ui.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.ui.utils.TLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author wufan
 * @date 2016/1/30
 */
public class LoginActivity extends BaseActivity {
    LoginReq req = new LoginReq();
    @Bind(R.id.login_phone_num_et)
    EditText mPhoneNumEt;
    @Bind(R.id.login_password_et)
    EditText mPasswordEt;

    private String mPhone=null;

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

        mPhoneNumEt.setText(PreferenceUtils.getString(this,PreferenceUtils.Key.PHONE));

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
        mPhone = mPhoneNumEt.getText().toString();
        if(isValid()){
            req.setPhone_num(mPhone);
            req.setPassword(mPasswordEt.getText().toString());
            loginCase();
        }
    }

    public boolean isValid() {
        if(CommonUtils.isEmpty(mPhone)){
            showToast("请输入您的手机号码");
            mPhoneNumEt.requestFocus();
            return false;
        }
        if(mPhone.length() !=11){
            showToast("请输入正确的手机号码");
            mPhoneNumEt.requestFocus();
            return false;
        }
        if(CommonUtils.isEmpty(mPasswordEt.getText().toString())){
            showToast("请输入您的密码");
            return false;
        }
        return true;
    }

    private void loginCase() {
        if (NetUtils.isNetworkConnected(this)) {
            ApiManager.getService(this).login(req, new Callback<LoginRes>() {
                @Override
                public void success(LoginRes loginRes, Response response) {
                    PreferenceUtils.putString(LoginActivity.this,
                            PreferenceUtils.Key.ACCESS, loginRes.getAccess_token());
                    //设置为已登录
                    PreferenceUtils.putBoolean(LoginActivity.this, PreferenceUtils.Key.LOGIN, true);
                    getUserInfo(loginRes.getUser_id());
                }

                @Override
                public void failure(RetrofitError error) {
                    showInnerError(error);
                }
            });

        } else {
            showNetWorkError();
        }
    }

    private void getUserInfo(String user_id) {
        ApiManager.getService(getApplicationContext()).getUserInfo(user_id, new Callback<UserInfoRes>() {
            @Override
            public void success(UserInfoRes userInfoRes, Response response) {
                saveUserInfo(userInfoRes);
                TLog.i("save:",userInfoRes.getName()+" "+mPhone);
                readyGoThenKill(MainActivity.class);
            }

            @Override
            public void failure(RetrofitError error) {
                showInnerError(error);
            }
        });
    }

    private void saveUserInfo(UserInfoRes userInfoRes) {
        PreferenceUtils.putString(this,PreferenceUtils.Key.PHONE,mPhone);
        PreferenceUtils.putString(this,PreferenceUtils.Key.NAME,userInfoRes.getName());
        PreferenceUtils.putString(this, PreferenceUtils.Key.ENROLL_YEAR, userInfoRes.getEnroll_year());
    }
}
