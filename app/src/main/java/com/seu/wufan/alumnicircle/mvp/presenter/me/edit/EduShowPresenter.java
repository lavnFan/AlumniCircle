package com.seu.wufan.alumnicircle.mvp.presenter.me.edit;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.EduRes;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
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
public class EduShowPresenter implements IEduShowPresenter {

    private IEduShowView iEduShowView;
    private Subscription subscription;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private Context appContext;

    @Inject
    public EduShowPresenter(@ForApplication Context context, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void saveEdu(final Edu edu) {
        if(NetUtils.isNetworkConnected(appContext)){
            subscription = userModel.addEduHistory(edu)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<EduRes>() {
                        @Override
                        public void call(EduRes eduRes) {
                            TLog.i("TAG","add edu"+eduRes.getId());
                            iEduShowView.backEdit();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iEduShowView.showToast(exception.getMessage());
                            } else {
                                iEduShowView.showNetError();
                            }
                        }
                    });
        }else{
            iEduShowView.showNetCantUse();
        }
    }

    @Override
    public void attachView(IView v) {
        iEduShowView = (IEduShowView) v;
    }

    @Override
    public void destroy() {
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
}
