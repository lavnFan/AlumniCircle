package com.seu.wufan.alumnicircle.mvp.presenter.impl;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.presenter.IRegisterPresenter;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IRegisterView;

import javax.inject.Inject;

import rx.Subscription;

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
    public void doRegister(String phone_num, String enroll_year, String school, String major, String password) {

    }

    @Override
    public void isValid(String phone_num, String enroll_year, String school, String major, String password) {

    }
}
