package com.seu.wufan.alumnicircle.mvp.presenter.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.item.TokenRes;
import com.seu.wufan.alumnicircle.api.entity.item.User;
import com.seu.wufan.alumnicircle.common.provider.UserTokenProvider;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtil;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.CircleModel;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ILoginView;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.LeanchatUser;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.comm.ui.activities.FindActivity;
import com.umeng.community.location.DefaultLocationImpl;

import java.util.Random;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/2/29
 */
public class LoginIPresenter implements ILoginIPresenter {

    private ILoginView mLoginView;
    private Subscription loginSubscription;
    private Subscription userSubscription;
    private Subscription weixinSubscription;

    private TokenModel tokenModel;
    private CircleModel circleModel;
    private ContactsModel contactsModel;
    private UserModel userModel;
    private Context appContext;
    private PreferenceUtils preferenceUtils;
    User user = new User();  //缓存本地的user

    @Inject
    public LoginIPresenter(@ForApplication Context context, TokenModel tokenModel, CircleModel circleModel, ContactsModel contactsModel, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.circleModel = circleModel;
        this.contactsModel = contactsModel;
        this.userModel = userModel;
    }

    @Override
    public void attachView(IView v) {
        mLoginView = (ILoginView) v;
    }

    @Override
    public void doLogin(final String phoneNum, String password) {
        if (NetUtils.isNetworkConnected(appContext)) {
            loginSubscription = tokenModel.login(phoneNum, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<LoginRes>() {
                        @Override
                        public void call(LoginRes loginRes) {
                            preferenceUtils.putString(loginRes.getAccess_token(), PreferenceType.ACCESS_TOKEN);
                            preferenceUtils.putString(loginRes.getUser_id(), PreferenceType.USER_ID);
                            PreferenceUtil.putString(appContext,loginRes.getUser_id(),PreferenceUtil.Key.USER_ID);

                            tokenModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                            circleModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                            contactsModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                            userModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));

                            saveUserInfo(loginRes.getUser_id());
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mLoginView.loginFailed();
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                mLoginView.showToast(exception.getMessage());
                            } else {
                                mLoginView.showNetError();
                            }
                        }
                    });
        } else {
            mLoginView.showNetCantUse();
        }
    }

    @Override
    public void destroy() {
        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
        }
    }

    public boolean isValid(String phoneNum, String password) {
        if (CommonUtils.isEmpty(phoneNum)) {
            mLoginView.showToast("请输入您的手机号码");
            return false;
        }
        if (phoneNum.length() != 11) {
            mLoginView.showToast("请输入正确的手机号码");
            return false;
        }
        if (CommonUtils.isEmpty(password)) {
            mLoginView.showToast("请输入您的密码");
            return false;
        }
        return true;
    }

    @Override
    public void doWeiXinLogin(String access_token, String open_id) {
        if (NetUtils.isNetworkConnected(appContext)) {
            weixinSubscription = tokenModel.loginByWeiXin(access_token, open_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<TokenRes>() {
                        @Override
                        public void call(final TokenRes tokenRes) {
                            TLog.i("TAG", tokenRes.getAccess_token() + " " + tokenRes.getUser_id());
                            tokenModel.setTokenProvider(new UserTokenProvider(tokenRes.getAccess_token()));
                            circleModel.setTokenProvider(new UserTokenProvider(tokenRes.getAccess_token()));
                            contactsModel.setTokenProvider(new UserTokenProvider(tokenRes.getAccess_token()));
                            userModel.setTokenProvider(new UserTokenProvider(tokenRes.getAccess_token()));
                            preferenceUtils.putString(tokenRes.getAccess_token(), PreferenceType.ACCESS_TOKEN);

                            //再去后台请求用户信息，是否有完善
                            Subscription userSubscription = tokenModel.getUserInfo(tokenRes.getUser_id())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfoRes>() {
                                        @Override
                                        public void call(UserInfoRes userInfoRes) {
                                            TLog.i("TAG", "user info:"+userInfoRes.getName() + userInfoRes.getSchool());
                                            if (!userInfoRes.getName().isEmpty() && !userInfoRes.getSchool().isEmpty() && !userInfoRes.getEnroll_year().isEmpty()) {
                                                TLog.i("TAG", userInfoRes.getName() + userInfoRes.getSchool() + "已完善，登录");
                                                //若已完善，则保存用户信息到本地，进行正常跳转
                                                preferenceUtils.putString(tokenRes.getUser_id(), PreferenceType.USER_ID);
                                                preferenceUtils.putString(userInfoRes.getImage(), PreferenceType.USER_PHOTO);
                                                preferenceUtils.putString(userInfoRes.getName(), PreferenceType.USER_NAME);
                                                PreferenceUtil.putString(appContext,PreferenceUtil.Key.USER_ID,PreferenceUtil.Key.USER_ID);
                                                user.setImage(userInfoRes.getImage());
                                                user.setSchool(userInfoRes.getSchool());
                                                user.setMajor(userInfoRes.getMajor());
                                                mockLoginData(tokenRes.getUser_id(), userInfoRes.getName());
                                            } else {
                                                //若未完善，则跳到注册界面，进行信息完善
                                                TLog.i("TAG", "未完善，跳转注册");
                                                mLoginView.goRegister(tokenRes.getUser_id());
                                            }
                                        }
                                    });
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mLoginView.loginFailed();
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                mLoginView.showToast(exception.getMessage());
                            } else {
                                mLoginView.showNetError();
                            }
                        }
                    });
        } else {
            mLoginView.showNetCantUse();
        }
    }

    private void mockLoginData(final String userId, String name) {
        //创建CommUser前必须先初始化CommunitySDK
        CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);

        CommUser loginUser = new CommUser();
        loginUser.id = userId; // 用户id
        loginUser.name = name; // 用户名

        user.setName(name);
        user.setUser_id(userId);

        sdk.loginToUmengServerBySelfAccount(appContext, loginUser.name, loginUser.id, new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int stCode, CommUser commUser) {
                if (ErrorCode.NO_ERROR == stCode) {
                    // 设置地理位置SDK
//                    LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl());

                    user.setUmeng_id(commUser.id);
                    PreferenceUtil.putBean(appContext, PreferenceUtil.Key.EXTRA_COMMUSER, user);

                    //在此处可以跳转到任何一个你想要的activity
                    mLoginView.loginSuccess();
                }

            }
        });
    }

    private void saveUserInfo(final String user_id) {
        userSubscription = tokenModel.getUserInfo(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfoRes>() {
                    @Override
                    public void call(final UserInfoRes userInfoRes) {
                        preferenceUtils.putString(userInfoRes.getImage(), PreferenceType.USER_PHOTO);
                        preferenceUtils.putString(userInfoRes.getName(), PreferenceType.USER_NAME);
                        user.setImage(userInfoRes.getImage());
                        user.setSchool(userInfoRes.getSchool());
                        user.setMajor(userInfoRes.getMajor());
                        mockLoginData(user_id, userInfoRes.getName());
                    }
                });
    }

}
