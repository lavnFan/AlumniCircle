package com.seu.wufan.alumnicircle.mvp.presenter.login;

import android.content.Context;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.bumptech.glide.Glide;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
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
import com.seu.wufan.alumnicircle.mvp.views.activity.IRegisterView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.community.location.DefaultLocationImpl;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/3/1
 */
public class RegisterIPresenter implements IRegisterIPresenter {

    private IRegisterView registerView;
    private Subscription registerSubmission;
    private Subscription weixinSubscription;

    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private CircleModel circleModel;
    private ContactsModel contactsModel;
    private UserModel userModel;
    private Context appContext;

    User user = new User();  //缓存本地的user

    @Inject
    public RegisterIPresenter(@ForApplication Context context, TokenModel tokenModel, CircleModel circleModel, ContactsModel contactsModel, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.circleModel = circleModel;
        this.contactsModel = contactsModel;
        this.userModel = userModel;
    }

    @Override
    public void attachView(IView v) {
        registerView = (IRegisterView) v;
    }

    @Override
    public void destroy() {
        if (registerSubmission != null) {
            registerSubmission.unsubscribe();
        }
    }

    @Override
    public void doRegister(final String phone_num, String password, String enroll_year, String school, String major, final String name, String number) {
        if (isValid(phone_num, password, enroll_year, school, major, name, number)) {
            if (NetUtils.isNetworkConnected(appContext)) {
                registerView.registerLoading();
                registerSubmission = (Subscription) tokenModel.register(phone_num, enroll_year, school, major, password, name, number)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<LoginRes>() {
                            @Override
                            public void call(LoginRes loginRes) {
                                preferenceUtils.putString(loginRes.getAccess_token(), PreferenceType.ACCESS_TOKEN);
                                preferenceUtils.putString(loginRes.getUser_id(), PreferenceType.USER_ID);
                                preferenceUtils.putString(name, PreferenceType.USER_NAME);
                                PreferenceUtil.putString(appContext,loginRes.getUser_id(),PreferenceUtil.Key.USER_ID);

                                tokenModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                                circleModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                                contactsModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));
                                userModel.setTokenProvider(new UserTokenProvider(loginRes.getAccess_token()));

                                saveUserInfo(loginRes.getUser_id(), name);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                registerView.registerFailed();
                                if (throwable instanceof retrofit2.HttpException) {
                                    retrofit2.HttpException exception = (HttpException) throwable;
                                    registerView.showToast(exception.getMessage());
                                } else {
                                    registerView.showNetError();
                                }
                            }
                        });
            } else {
                registerView.showNetCantUse();
            }
        }
    }

    @Override
    public boolean isValid(String phone_num, String password, String enroll_year, String school, String major, String name, String number) {
        if (CommonUtils.isEmpty(phone_num)) {
            registerView.showToast("请输入您的手机号码");
            return false;
        }
        if (phone_num.length() != 11) {
            registerView.showToast("请输入正确的手机号码");
            return false;
        }
        if (CommonUtils.isEmpty(password)) {
            registerView.showToast("请输入您的密码");
            return false;
        }
        if (password.length() < 6 || password.length() > 12) {
            registerView.showToast("密码在6-12位");
            return false;
        }
        if (CommonUtils.isEmpty(name)) {
            registerView.showToast("请填写用户名");
            return false;
        }
        if (name.length() < 2) {
            registerView.showToast("用户名长度太短");
            return false;
        }
        if (CommonUtils.isEmpty(number)) {
            registerView.showToast("请填写学号");
            return false;
        }
        if (CommonUtils.isEmpty(enroll_year)) {
            registerView.showToast("请选择入学年份");
            return false;
        }
        if (CommonUtils.isEmpty(school)) {
            registerView.showToast("请选择");
            return false;
        }
        return true;
    }

    @Override
    public void doWeixin(final UserInfoRes userInfoRes) {
        //更新用户信息
        if (NetUtils.isNetworkConnected(appContext)) {
            registerView.registerLoading();
            weixinSubscription = tokenModel.updateUserInfo(userInfoRes)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            preferenceUtils.putString(userInfoRes.getUser_id(), PreferenceType.USER_ID);
                            preferenceUtils.putString(userInfoRes.getName(), PreferenceType.USER_NAME);
                            PreferenceUtil.putString(appContext,userInfoRes.getUser_id(),PreferenceUtil.Key.USER_ID);
                            saveUserInfo(userInfoRes.getUser_id(),userInfoRes.getName());
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            registerView.registerFailed();
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                registerView.showToast(exception.getMessage());
                            } else {
                                registerView.showNetError();
                            }
                        }
                    });
        }
    }

    private void saveUserInfo(String userId, String name) {
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
                    LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl());

                    user.setUmeng_id(commUser.id);
                    PreferenceUtil.putBean(appContext, PreferenceUtil.Key.EXTRA_COMMUSER, user);

                    registerView.registerSuccess();
                }

            }
        });
    }

}
