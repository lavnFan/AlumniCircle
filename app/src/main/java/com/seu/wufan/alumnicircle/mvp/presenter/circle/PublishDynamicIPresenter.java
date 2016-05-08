package com.seu.wufan.alumnicircle.mvp.presenter.circle;

import android.content.Context;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.UploadImageUntil;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IPublishDynamicView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/4/29
 */
public class PublishDynamicIPresenter implements IPublishDynamicIPresenter {

    IPublishDynamicView publishDynamicView;
    Subscription createQinuSubscription;
    ArrayList<String> photoPaths;
    Subscription publishDynamicSubscription;
    UploadImageUntil uploadImageUntil;

    private CircleModel circleModel;
    private Context appContext;
    private PreferenceUtils preferenceUtils;

    private List<String> keys;
    private boolean cancel_qiniu = false;
    private List<String> selected_paths = new ArrayList<>();
    private String news_text;
    private String topic_id;

    @Inject
    public PublishDynamicIPresenter(@ForApplication Context context, CircleModel circleModel, PreferenceUtils preferenceUtils, UploadImageUntil uploadImageUntil) {
        this.circleModel = circleModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.uploadImageUntil = uploadImageUntil;
    }

    @Override
    public void publishDynamic(int count, final String news_text, final List<String> paths, final String topic_id) {
        if (NetUtils.isNetworkConnected(appContext)) {
            //先上传七牛图片凭证，再进行动态请求
            selected_paths = paths;
            this.news_text = news_text;
            this.topic_id = topic_id;
            createQinuSubscription = circleModel.createQiNiuToken(count)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<QnRes>>() {
                        @Override
                        public void call(List<QnRes> qnRes) {
                            uploadImageToken(qnRes, paths);
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
        } else {
            publishDynamicView.showNetCantUse();
        }
    }

    private void uploadImageToken(List<QnRes> qnRes, List<String> paths) {
        for (int i = 0; i < qnRes.size(); i++) {
            uploadImageSingle(qnRes.get(i).getKey(), qnRes.get(i).getToken(), paths.get(i));
        }
    }

    private void uploadImageSingle(String key, String token, String path) {
        uploadImageUntil.upLoadImage(path, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    finishFromSingleThread(true, key);
                } else if (info.isNetworkBroken()) {
                    finishFromSingleThread(false, null);
                } else if (info.isServerError()) {
                    finishFromSingleThread(false, null);
                }
            }
        }, new UploadOptions(null, null, false, null, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return cancel_qiniu;
            }
        }));
    }

    private synchronized void finishFromSingleThread(boolean result, String key) {
        if (result && key != null) {
            keys.add(key);
            if (keys.size() ==selected_paths.size()){
                publishDynamicDone(news_text,keys,topic_id);
            }
        }else{
            cancel_qiniu = true;
        }
    }

    private void publishDynamicDone(String news_text, List<String> key, String topic_id) {
            if(NetUtils.isNetworkConnected(appContext)){
                publishDynamicSubscription = circleModel.publishDynamic(news_text,key,topic_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                publishDynamicView.showToast("发布成功");
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
            }else{
                publishDynamicView.showNetError();
            }
    }

    @Override
    public void attachView(IView v) {
        publishDynamicView = (IPublishDynamicView) v;
    }

    @Override
    public void destroy() {
        if (publishDynamicSubscription != null) {
            publishDynamicSubscription.unsubscribe();
        }
        if(createQinuSubscription!=null){
            createQinuSubscription.isUnsubscribed();
        }
    }
}
