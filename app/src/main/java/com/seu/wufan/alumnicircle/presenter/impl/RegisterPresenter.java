package com.seu.wufan.alumnicircle.presenter.impl;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.model.TokenModel;
import com.seu.wufan.alumnicircle.presenter.IRegisterPresenter;
import com.seu.wufan.alumnicircle.ui.views.IView;
import com.seu.wufan.alumnicircle.ui.views.activity.IRegisterView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author wufan
 * @date 2016/3/1
 */
public class RegisterPresenter implements IRegisterPresenter{

    private IRegisterView registerView;
    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private Subscription registerSubmission;
    private Context appContext;

    @Inject
    public RegisterPresenter(@ForApplication Context context,TokenModel tokenModel,PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void doRegister(RegisterReq req) {

    }

    @Override
    public void isValid(RegisterReq req) {

    }

    @Override
    public void attachView(IView v) {
        registerView = (IRegisterView) v;
    }

    @Override
    public void destroy() {

    }
}
