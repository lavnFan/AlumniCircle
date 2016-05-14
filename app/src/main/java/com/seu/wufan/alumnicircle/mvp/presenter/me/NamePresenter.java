package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.item.User;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtil;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.INameView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/13
 */
public class NamePresenter implements INamePresenter {

    private INameView iNameView;

    private Subscription nameSubscription;
    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private Context appContext;

    @Inject
    public NamePresenter(@ForApplication Context context, TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void updateName(final UserInfoRes userInfoRes) {
        nameSubscription = tokenModel.updateUserInfo(userInfoRes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        saveName(userInfoRes.getName());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof retrofit2.HttpException) {
                            retrofit2.HttpException exception = (HttpException) throwable;
                            iNameView.showToast(exception.getMessage());
                        } else {
                            iNameView.showNetError();
                        }
                    }
                });
    }

    /**
     * 修改用户名
     * @param name
     * 先修改本地缓存
     * 再同步更新leanchat与友盟
     */
    private void saveName(String name) {
        preferenceUtils.putString(name, PreferenceType.USER_NAME);
        User user = (User) PreferenceUtil.getBean(appContext,PreferenceUtil.Key.EXTRA_COMMUSER);
        user.setName(name);

        CommUser commUser = new CommUser();
        commUser.name = name;
        commUser.id = user.getUser_id();

        CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);
        sdk.updateUserProfile(commUser, new Listeners.CommListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(Response response) {
                if(response.errCode== ErrorCode.NO_ERROR){
                    iNameView.destroy();
                }
            }
        });
    }

    @Override
    public void attachView(IView v) {
        iNameView = (INameView) v;
    }

    @Override
    public void destroy() {
        if(nameSubscription!=null){
            nameSubscription.unsubscribe();
        }
    }
}
