package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.LoginReq;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.presenter.impl.LoginPresenter;
import com.seu.wufan.alumnicircle.ui.views.activity.ILoginView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/30
 */
public class LoginActivity extends BaseActivity implements ILoginView{
    LoginReq req = new LoginReq();
    @Bind(R.id.login_phone_num_et)
    EditText mPhoneNumEt;
    @Bind(R.id.login_password_et)
    EditText mPasswordEt;

    private String mPhone=null;

    @Inject
    LoginPresenter loginPresenter;

    public static Intent getCallingIntent(Context context){
        return new Intent(context,LoginActivity.class);
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

    }

    @OnClick(R.id.register_linear_layout)
    void register() {
        readyGo(RegisterActivity.class);
    }

    @OnClick(R.id.login_btn)
    void login() {


    }

//    public boolean isValid() {
//        if(CommonUtils.isEmpty(mPhone)){
//            showToast("请输入您的手机号码");
//            mPhoneNumEt.requestFocus();
//            return false;
//        }
//        if(mPhone.length() !=11){
//            showToast("请输入正确的手机号码");
//            mPhoneNumEt.requestFocus();
//            return false;
//        }
//        if(CommonUtils.isEmpty(mPasswordEt.getText().toString())){
//            showToast("请输入您的密码");
//            return false;
//        }
//        return true;
//    }
//
//    private void loginCase() {
//        if (NetUtils.isNetworkConnected(this)) {
//            ApiManager.getService(this).login(req, new Callback<LoginRes>() {
//                @Override
//                public void success(LoginRes loginRes, Response response) {
//                    TLog.i("Login:",loginRes.getAccess_token()+" "+loginRes.getUser_id());
//                    getUserInfo(loginRes.getUser_id());
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    showInnerError(error);
//                }
//            });
//
//        } else {
//            showNetWorkError();
//        }
//    }
//
//    private void getUserInfo(String user_id) {
//        ApiManager.getService(getApplicationContext()).getUserInfo(user_id, new Callback<UserInfoRes>() {
//            @Override
//            public void success(UserInfoRes userInfoRes, Response response) {
//                saveUserInfo(userInfoRes);
//                TLog.i("save:", userInfoRes.getName() + " " + mPhone);
//                readyGoThenKill(MainActivity.class);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                showInnerError(error);
//            }
//        });
//    }

    @Override
    public String getUser_id() {
        return null;
    }

    @Override
    public void loginSuccess() {

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
        ToastUtils.showToast(s,this);
    }
}
