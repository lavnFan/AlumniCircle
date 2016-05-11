package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IMyView;
import com.umeng.comm.core.beans.CommUser;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/11
 */
public class MyPresenter implements IMyPresenter {

    private Subscription preferenceSubscription;
    private IMyView iMyView;

    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private Context appContext;

    @Inject
    public MyPresenter(@ForApplication Context context, TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void attachView(IView v) {
        iMyView = (IMyView) v;
    }

    @Override
    public void destroy() {
        if(preferenceSubscription!=null){
            preferenceSubscription.unsubscribe();
        }
    }

    @Override
    public String getUid() {
        preferenceSubscription = preferenceUtils.getUserId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        CommUser user = new CommUser();
                        user.id = s;
                        iMyView.setUser(user);
                    }
                });
        return null;
    }
}
