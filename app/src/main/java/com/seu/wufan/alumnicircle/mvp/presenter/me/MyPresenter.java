package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IMyView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.ProfileResponse;

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
    private Subscription userIdSubscription;
    private Subscription userInfoSubscription;
    private Subscription nameSubscription;
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
        if (preferenceSubscription != null) {
            preferenceSubscription.unsubscribe();
        }
        if (userIdSubscription != null) {
            userIdSubscription.unsubscribe();
        }
        if (nameSubscription != null) {
            nameSubscription.unsubscribe();
        }
    }

    @Override
    public void init() {
        userIdSubscription = preferenceUtils.getUserPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        iMyView.setPhoto(s);
                    }
                });
        userIdSubscription = preferenceUtils.getUserId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (NetUtils.isNetworkConnected(appContext)) {
                            userInfoSubscription = tokenModel.getUserInfo(s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfoRes>() {
                                        @Override
                                        public void call(UserInfoRes userInfoRes) {
                                            iMyView.setPhoto(userInfoRes.getImage());
                                        }
                                    });
                        }
                    }
                });

        nameSubscription = preferenceUtils.getUserName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        iMyView.setName(s);
                    }
                });


    }

    @Override
    public void goDynamic() {
        if (NetUtils.isNetworkConnected(appContext)) {
            preferenceSubscription = preferenceUtils.getUserId()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);
                            //获取友盟id
                            sdk.fetchUserProfile(s, "self_account", new Listeners.FetchListener<ProfileResponse>() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(ProfileResponse profileResponse) {
                                    iMyView.goDynamic(profileResponse.result);
                                }
                            });
                        }
                    });
        } else {
            iMyView.showNetCantUse();
        }
    }
}
