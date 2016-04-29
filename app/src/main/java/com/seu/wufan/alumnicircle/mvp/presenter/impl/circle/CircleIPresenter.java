package com.seu.wufan.alumnicircle.mvp.presenter.impl.circle;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ICircleView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class CircleIPresenter implements ICircleIPresenter {

    private ICircleView circleView;
    private Subscription subscription;

    private CircleModel circleModel;
    private Context appContext;
    private PreferenceUtils preferenceUtils;

    @Inject
    public CircleIPresenter(@ForApplication Context context, CircleModel circleModel, PreferenceUtils preferenceUtils) {
        this.circleModel = circleModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void getUserTimeDynamic() {

    }

    @Override
    public void getTopic() {

    }

    @Override
    public void attachView(IView v) {
        circleView = (ICircleView) v;
    }

    @Override
    public void destroy() {
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
}
