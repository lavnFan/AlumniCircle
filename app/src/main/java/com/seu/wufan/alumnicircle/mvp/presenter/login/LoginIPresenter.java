package com.seu.wufan.alumnicircle.mvp.presenter.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.common.provider.UserTokenProvider;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ILoginView;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/2/29
 */
public class LoginIPresenter implements ILoginIPresenter {

    private ILoginView mLoginView;
    private Subscription loginSubscription;

    private TokenModel tokenModel;
    private CircleModel circleModel;
    private ContactsModel contactsModel;
    private UserModel userModel;
    private Context appContext;
    private PreferenceUtils preferenceUtils;

    @Inject
    public LoginIPresenter(@ForApplication Context context, TokenModel tokenModel, CircleModel circleModel, ContactsModel contactsModel, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.circleModel = circleModel;
        this.contactsModel = contactsModel;
        this.userModel = userModel;
    }

    @Override
    public void attachView(IView v) {
        mLoginView = (ILoginView) v;
    }

    @Override
    public void doLogin(final String phoneNum, String password) {
            if (NetUtils.isNetworkConnected(appContext)) {
                loginSubscription = tokenModel.login(phoneNum, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<LoginRes>() {
                            @Override
                            public void call(LoginRes loginRes) {
                                preferenceUtils.putString(phoneNum,PreferenceType.PHONE);
                                preferenceUtils.putString(loginRes.getAccess_token(), PreferenceType.ACCESS_TOKEN);
                                preferenceUtils.putString(loginRes.getUser_id(),PreferenceType.USER_ID);
                                tokenModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                                circleModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                                contactsModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                                userModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));

                                //设置聊天的信息
                                ChatManager.getInstance().openClient(appContext, loginRes.getUser_id(), new AVIMClientCallback() {
                                    @Override
                                    public void done(AVIMClient avimClient, AVIMException e) {
                                        if (null == e) {
                                            mLoginView.loginSuccess();
                                        } else {
                                            mLoginView.showToast(e.toString());
                                        }
                                    }
                                });
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                mLoginView.loginFailed();
                                if (throwable instanceof retrofit2.HttpException) {
                                    retrofit2.HttpException exception = (HttpException) throwable;
                                    mLoginView.showToast(exception.getMessage());
                                } else {
                                    mLoginView.showNetError();
                                }
                            }
                        });
            } else {
                mLoginView.showNetCantUse();
            }
    }

    @Override
    public void destroy() {
        if(loginSubscription!=null){
            loginSubscription.unsubscribe();
        }
    }

    public boolean isValid(String phoneNum, String password) {
        if(CommonUtils.isEmpty(phoneNum)){
            mLoginView.showToast("请输入您的手机号码");
            return false;
        }
        if(phoneNum.length() !=11){
            mLoginView.showToast("请输入正确的手机号码");
            return false;
        }
        if(CommonUtils.isEmpty(password)){
            mLoginView.showToast("请输入您的密码");
            return false;
        }
        return true;
    }
}
