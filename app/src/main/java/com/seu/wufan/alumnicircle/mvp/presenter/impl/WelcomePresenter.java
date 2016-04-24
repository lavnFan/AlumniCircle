package com.seu.wufan.alumnicircle.mvp.presenter.impl;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.presenter.IWelcomePresenter;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IWelcomeView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author wufan
 * @date 2016/4/24
 */
public class WelcomePresenter implements IWelcomePresenter{

    private PreferenceUtils preferenceUtils;
    private Context appContext;
    private TokenModel tokenModel;

    private IWelcomeView welcomeView;
    private Subscription welcomeSubscription;

    @Inject
    public WelcomePresenter(@ForApplication Context context, TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void jumpActivity() {

    }

    @Override
    public void attachView(IView v) {
        welcomeView = (IWelcomeView) v;
    }

    @Override
    public void destroy() {
        if(welcomeSubscription!=null){
            welcomeSubscription.unsubscribe();
        }
    }
}
