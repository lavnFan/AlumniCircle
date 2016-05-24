package com.seu.wufan.alumnicircle.mvp.presenter.circle;

import android.content.Context;
import android.util.Log;

import com.seu.wufan.alumnicircle.api.entity.DynamicListRes;
import com.seu.wufan.alumnicircle.api.entity.TopicRes;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ICircleView;

import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class CirclePresenter implements ICircleIPresenter {

    private ICircleView circleView;
    private Subscription dynamicSubscription;
    private Subscription topicSubscription;

    private CircleModel circleModel;
    private Context appContext;
    private PreferenceUtils preferenceUtils;

    @Inject
    public CirclePresenter(@ForApplication Context context, CircleModel circleModel, PreferenceUtils preferenceUtils) {
        this.circleModel = circleModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void getUserTimeDynamic(String page) {
        if(NetUtils.isNetworkConnected(appContext)){
            dynamicSubscription = circleModel.getNewDynamic(page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<DynamicItem>>() {
                        @Override
                        public void call(List<DynamicItem> dynamicItems) {
                            circleView.showDynamic(dynamicItems,true);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.i("throw:", throwable.getMessage() + throwable.getCause() + throwable.getStackTrace().toString());
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                circleView.showToast(exception.getMessage());
                            } else {
                                circleView.showNetError();
                            }
                        }
                    });
        }else{
            circleView.showNetCantUse();
        }
    }

    @Override
    public void getTopic() {
        if(NetUtils.isNetworkConnected(appContext)){
            topicSubscription = circleModel.getTopic()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<TopicRes>() {
                        @Override
                        public void call(TopicRes topicRes) {
                            circleView.initTopic(topicRes);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if(throwable instanceof retrofit2.HttpException){
                                retrofit2.HttpException exception = (HttpException) throwable;
                                circleView.showToast(exception.getMessage());
                            }else{
                                circleView.showNetError();
                            }
                        }
                    });
        }
    }

    @Override
    public void attachView(IView v) {
        circleView = (ICircleView) v;
    }

    @Override
    public void destroy() {
        if(dynamicSubscription!=null){
            dynamicSubscription.unsubscribe();
        }
        if(topicSubscription!=null){
            topicSubscription.unsubscribe();
        }
    }
}
