package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.UploadImageUntil;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IEditInformationView;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.NameActivity;
import com.seu.wufan.alumnicircle.ui.fragment.MyFragment;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/12
 */
public class EditInformationPresenter implements IEditInformationPresenter {

    IEditInformationView editInformationView;

    private Subscription userInfoSubscription;
    private Subscription userDetailSubscription;
    private Subscription tokenSubscription;
    private Subscription updateSubscription;

    private PreferenceUtils preferenceUtils;
    private UploadImageUntil uploadImageUntil;
    private UserModel userModel;
    private TokenModel tokenModel;
    private Context appContext;

    UserInfoRes userInfo = new UserInfoRes();
    UserInfoDetailRes userDetail =new UserInfoDetailRes();

    @Inject
    public EditInformationPresenter(@ForApplication Context context, UploadImageUntil uploadImageUntil, TokenModel tokenModel, UserModel userModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.uploadImageUntil = uploadImageUntil;
        this.tokenModel = tokenModel;
    }

    @Override
    public void savePhoto(final String photo_path) {
        tokenSubscription = tokenModel.createQiNiuToken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<QnRes>>() {
                    @Override
                    public void call(List<QnRes> qnRes) {
                        uploadImageUntil.upLoadImage(photo_path, qnRes.get(0).getKey(), qnRes.get(0).getToken(), new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                preferenceUtils.putString(key, PreferenceType.USER_PHOTO);
                                userInfo.setImage(key);
                                updateSubscription=tokenModel.updateUserInfo(userInfo)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Void>() {
                                            @Override
                                            public void call(Void aVoid) {
                                                updatePhoto(photo_path);
                                            }
                                        });
                            }
                        }, new UploadOptions(null, null, false, null, null));
                    }
                });
    }

    /**
     * 修改图片
     * @param photo_path
     * 同时上传到友盟和leancloud上，保持同步
     */
    private void updatePhoto(String photo_path) {
        preferenceUtils.putString(photo_path,PreferenceType.USER_PHOTO);
        editInformationView.setPhotoResult(photo_path);
    }

    @Override
    public void init() {
        Subscription s = preferenceUtils.getUserId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        userInfoSubscription = tokenModel.getUserInfo(s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<UserInfoRes>() {
                                    @Override
                                    public void call(UserInfoRes userInfoRes) {
                                        userInfo = userInfoRes;
                                        TLog.i("TAG","init"+userInfoRes.getImage());
                                        editInformationView.initUserInfo(userInfoRes);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {

                                    }
                                });
                        userDetailSubscription = userModel.getUserInfoResObservable(s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<UserInfoDetailRes>() {
                                    @Override
                                    public void call(UserInfoDetailRes getUserInfoDetailRes) {
                                        userDetail = getUserInfoDetailRes;
                                        editInformationView.initDetail(userDetail);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {

                                    }
                                });
                    }
                });
    }

    @Override
    public UserInfoRes getUserInfo() {
        return userInfo;
    }

    @Override
    public UserInfoDetailRes getUserDetail() {
        return userDetail;
    }

    @Override
    public void attachView(IView v) {
        editInformationView = (IEditInformationView) v;
    }

    @Override
    public void destroy() {
        if(userInfoSubscription!=null){
            userInfoSubscription.unsubscribe();
        }
        if(userDetailSubscription!=null){
            userDetailSubscription.unsubscribe();
        }
        if(tokenSubscription!=null){
            tokenSubscription.unsubscribe();
        }
    }
}
