package com.seu.wufan.alumnicircle.mvp.presenter.circle;

import android.content.Context;
import android.util.Log;

import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IPublishDynamicView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/4/29
 */
public class PublishDynamicIPresenter implements IPublishDynamicIPresenter {

    IPublishDynamicView publishDynamicView;
    Subscription createQinuSubscription;
    ArrayList<String> photoPaths;
    Subscription subscription;

    private CircleModel circleModel;
    private Context appContext;
    private PreferenceUtils preferenceUtils;

    @Inject
    public PublishDynamicIPresenter(@ForApplication Context context, CircleModel circleModel, PreferenceUtils preferenceUtils) {
        this.circleModel = circleModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void publishDynamic(final String news_text, List<String> images, final String topic_id) {
        if (NetUtils.isNetworkConnected(appContext)) {
            photoPaths = new ArrayList<>();    //先上传七牛图片凭证，再进行动态请求
            for (String s : images) {
                createQinuSubscription = circleModel.createQiNiuToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<QnRes>() {
                            @Override
                            public void call(QnRes qnRes) {
                                Log.i("qnRes:", qnRes.getToken());
                                photoPaths.add(qnRes.getToken());
                                subscription = circleModel.publishDynamic(news_text, photoPaths, topic_id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Void>() {
                                            @Override
                                            public void call(Void aVoid) {
                                                publishDynamicView.publishSuccess();
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                publishDynamicView.publishFailed();
                                                if (throwable instanceof retrofit2.HttpException) {
                                                    retrofit2.HttpException exception = (HttpException) throwable;
                                                    publishDynamicView.showToast(exception.getMessage());
                                                } else {
                                                    publishDynamicView.showNetError();
                                                }
                                            }
                                        });
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                publishDynamicView.publishFailed();
                                if (throwable instanceof retrofit2.HttpException) {
                                    retrofit2.HttpException exception = (HttpException) throwable;
                                    publishDynamicView.showToast(exception.getMessage());
                                } else {
                                    publishDynamicView.showNetError();
                                }
                            }
                        });
            }
        } else {
            publishDynamicView.showNetCantUse();
        }
    }

    private void uploadImage(List<String> images) {

    }

    @Override
    public void attachView(IView v) {
        publishDynamicView = (IPublishDynamicView) v;
    }

    @Override
    public void destroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
