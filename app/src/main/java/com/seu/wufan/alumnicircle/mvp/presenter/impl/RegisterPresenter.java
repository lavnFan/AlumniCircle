package com.seu.wufan.alumnicircle.mvp.presenter.impl;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.presenter.IRegisterPresenter;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IRegisterView;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/3/1
 */
public class RegisterPresenter implements IRegisterPresenter{

    private IRegisterView registerView;
    private Subscription registerSubmission;

    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private Context appContext;

    @Inject
    public RegisterPresenter(@ForApplication Context context,TokenModel tokenModel,PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void attachView(IView v) {
        registerView = (IRegisterView) v;
    }

    @Override
    public void destroy() {
        if(registerSubmission!=null){
            registerSubmission.unsubscribe();
        }
    }

    @Override
    public void doRegister(final String phone_num, String password, String enroll_year, String school, String major) {
        if(isValid(phone_num,password,enroll_year,school,major)){
            if(NetUtils.isNetworkConnected(appContext)){
                registerSubmission = (Subscription) tokenModel.register(phone_num,enroll_year,school,major,password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<LoginRes>() {
                            @Override
                            public void call(LoginRes loginRes) {
                                preferenceUtils.putString(phone_num, PreferenceType.PHONE);
                                preferenceUtils.putString(loginRes.getAccess_token(),PreferenceType.ACCESS_TOKEN);
                                preferenceUtils.putString(loginRes.getUser_id(),PreferenceType.USER_ID);
                                registerView.registerSuccess();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (throwable instanceof retrofit2.HttpException) {
                                    retrofit2.HttpException exception = (HttpException) throwable;
                                    registerView.showToast(exception.getMessage());
                                } else {
                                    registerView.showNetError();
                                }
                            }
                        });
            }else{
                registerView.showNetCantUse();
            }
        }
    }

    @Override
    public boolean isValid(String phone_num,String password,String enroll_year,String school,String major) {
        if(CommonUtils.isEmpty(phone_num)){
            registerView.showToast("请输入您的手机号码");
            return false;
        }
        if(phone_num.length() !=11){
            registerView.showToast("请输入正确的手机号码");
            return false;
        }
        if(CommonUtils.isEmpty(password)){
            registerView.showToast("请输入您的密码");
            return false;
        }
        if(password.length()<6 || password.length() >12){
            registerView.showToast("密码在6-12位");
            return false;
        }
        if(CommonUtils.isEmpty(enroll_year)){
            registerView.showToast("请选择入学年份");
            return false;
        }
        if(CommonUtils.isEmpty(school)){
            registerView.showToast("请选择");
            return false;
        }
        return true;
    }
}