package com.seu.wufan.alumnicircle.mvp.presenter.login;

import android.content.Context;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.seu.wufan.alumnicircle.common.provider.UserTokenProvider;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IWelcomeView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/4/24
 */
public class WelcomeIPresenter implements IWelcomeIPresenter {

    private PreferenceUtils preferenceUtils;
    private Context appContext;
    private TokenModel tokenModel;
    private CircleModel circleModel;
    private ContactsModel contactsModel;
    private UserModel userModel;
    private IWelcomeView welcomeView;
    private Subscription welcomeSubscription;
    private Subscription userSubscription;

    @Inject
    public WelcomeIPresenter(@ForApplication Context context, TokenModel tokenModel, CircleModel circleModel, ContactsModel contactsModel, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.circleModel = circleModel;
        this.contactsModel = contactsModel;
        this.userModel = userModel;
    }

    /**
     * 跳转逻辑：先判断是否有账号信息，如果有且正确，则直接跳转到主页面；否则跳转到登录界面
     */
    @Override
    public void jumpActivity() {
        welcomeSubscription = (Subscription) preferenceUtils.getAccessToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(s.isEmpty()){
                            welcomeView.readyToLogin();
                        }else{
                            tokenModel.setTokenProvider(new UserTokenProvider(s));
                            circleModel.setTokenProvider(new UserTokenProvider(s));
                            contactsModel.setTokenProvider(new UserTokenProvider(s));
                            userModel.setTokenProvider(new UserTokenProvider(s));
                            welcomeView.readyToMain();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void attachView(IView v) {
        welcomeView = (IWelcomeView) v;
    }

    @Override
    public void destroy() {
        if(welcomeSubscription!=null){
            welcomeSubscription.unsubscribe();
        }
    }
}
