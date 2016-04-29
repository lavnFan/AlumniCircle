package com.seu.wufan.alumnicircle.mvp.presenter.impl.login;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.common.provider.UserTokenProvider;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ILoginView;

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
    private Context appContext;
    private PreferenceUtils preferenceUtils;

    @Inject
    public LoginIPresenter(@ForApplication Context context, TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
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
                                mLoginView.loginSuccess();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
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
