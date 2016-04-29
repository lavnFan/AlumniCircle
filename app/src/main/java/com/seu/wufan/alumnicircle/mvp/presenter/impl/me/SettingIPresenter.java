package com.seu.wufan.alumnicircle.mvp.presenter.impl.me;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISettingView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author wufan
 * @date 2016/4/25
 */
public class SettingIPresenter implements ISettingIPresenter {

    ISettingView settingView;
    Subscription subscription;

    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private Context appContext;

    @Inject
    public SettingIPresenter(@ForApplication Context context, TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    /**
     * 退出到登录界面，同时销毁缓存数据
     */
    @Override
    public void exit() {
        preferenceUtils.putString("", PreferenceType.ACCESS_TOKEN);
        settingView.exitToLogin();
    }

    @Override
    public void attachView(IView v) {
        settingView = (ISettingView) v;
    }

    @Override
    public void destroy() {
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
}
