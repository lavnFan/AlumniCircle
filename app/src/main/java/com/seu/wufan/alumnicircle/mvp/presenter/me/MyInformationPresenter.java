package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.GetUserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IMyInformationView;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/11
 */
public class MyInformationPresenter implements IMyInformationPresenter {

    private Subscription myinfoSubscription;
    private Subscription infoSubscription;

    private IMyInformationView iMyInformationView;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private TokenModel tokenModel;
    private Context appContext;

    @Inject
    public MyInformationPresenter(@ForApplication Context context, UserModel userModel,TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.tokenModel = tokenModel;
    }

    @Override
    public void getUserDetail(String user_id) {
        myinfoSubscription = userModel.getUserInfoResObservable(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GetUserInfoDetailRes>() {
                    @Override
                    public void call(GetUserInfoDetailRes getUserInfoDetailRes) {
                        iMyInformationView.initMyInfo(getUserInfoDetailRes);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof retrofit2.HttpException) {
                            retrofit2.HttpException exception = (HttpException) throwable;
                            iMyInformationView.showToast(exception.getMessage());
                        }else{
                            iMyInformationView.showNetCantUse();
                        }
                    }
                });
    }

    @Override
    public void getUserInfo(String user_id) {
        if(NetUtils.isNetworkConnected(appContext)){

        infoSubscription = tokenModel.getUserInfo(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfoRes>() {
                    @Override
                    public void call(UserInfoRes userInfoRes) {
                        iMyInformationView.initMyInfo(userInfoRes);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof retrofit2.HttpException) {
                            retrofit2.HttpException exception = (HttpException) throwable;
                            iMyInformationView.showToast(exception.getMessage());
                        }else{
                            iMyInformationView.showNetCantUse();
                        }
                    }
                });

        }else{
            iMyInformationView.showNetError();
        }
    }


    @Override
    public void attachView(IView v) {
        iMyInformationView = (IMyInformationView) v;
    }

    @Override
    public void destroy() {
        if(myinfoSubscription!=null){
            myinfoSubscription.unsubscribe();
        }
        if(infoSubscription!=null){
            infoSubscription.unsubscribe();
        }
    }
}
