package com.seu.wufan.alumnicircle.presenter.impl;

import android.content.Context;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.common.provider.UserTokenProvider;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.model.TokenModel;
import com.seu.wufan.alumnicircle.presenter.ILoginPresenter;
import com.seu.wufan.alumnicircle.ui.views.activity.ILoginView;

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
public class LoginPresenter implements ILoginPresenter {

    private ILoginView mLoginView;
    private TokenModel tokenModel;
    private Context appContext;

    private Subscription loginSubscription;

    @Inject
    public LoginPresenter(ILoginView mLoginView,TokenModel tokenModel,Context appContext) {
        this.mLoginView = mLoginView;
        this.tokenModel = tokenModel;
        this.appContext = appContext;
    }

    @Override
    public void attachView(View v) {
        mLoginView = (ILoginView) v;
    }

    @Override
    public void doLogin(String phoneNum, String password) {
        if(NetUtils.isNetworkConnected(appContext)){
            loginSubscription = tokenModel.login(phoneNum,password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<LoginRes>() {
                        @Override
                        public void call(LoginRes loginRes) {
                            mLoginView.showToast(appContext.getString(R.string.login_success));
                            tokenModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if(throwable instanceof retrofit2.HttpException){
                                retrofit2.HttpException exception = (HttpException) throwable;
                                mLoginView.showToast(exception.getMessage());
                            }else {
                                mLoginView.showNetError();
                            }
                        }
                    });
        }else{
            mLoginView.showNetCantUse();
        }
    }

    @Override
    public void onDestroy() {
        if(loginSubscription!=null){
            loginSubscription.unsubscribe();
        }
    }

}
