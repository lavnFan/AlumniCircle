package com.seu.wufan.alumnicircle.mvp.presenter.me.edit;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEduEditView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEduShowView;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/15
 */
public class EduEditPresenter implements IEduEditPresenter{

    private IEduEditView iEduEditView;
    private Subscription subscription;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private Context appContext;

    @Inject
    public EduEditPresenter(@ForApplication Context context, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void delete(String id) {
        if(NetUtils.isNetworkConnected(appContext)){
        subscription = userModel.deleteEduHistory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if(throwable instanceof retrofit2.HttpException){
                            retrofit2.HttpException exception = (HttpException) throwable;
                            iEduEditView.showToast(exception.getMessage());
                        }else{
                            iEduEditView.showNetError();
                        }
                    }
                });
        }else{
            iEduEditView.showNetCantUse();
        }
    }

    @Override
    public void attachView(IView v) {
        iEduEditView = (IEduEditView) v;
    }

    @Override
    public void destroy() {
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
}
