package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadOptions;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.item.User;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtil;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.UploadImageUntil;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEditInformationView;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.LeanchatUser;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.PortraitUploadResponse;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
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
    private Subscription updateGenderSubscription;
    private Subscription updateCitySubscription;
    private Subscription updateBirthdaySubscription;
    private Subscription updateProfessionSubscription;

    private PreferenceUtils preferenceUtils;
    private UploadImageUntil uploadImageUntil;
    private UserModel userModel;
    private TokenModel tokenModel;
    private Context appContext;

    UserInfoRes userInfo = new UserInfoRes();
    UserInfoDetailRes userDetail = new UserInfoDetailRes();
    private String user_id = null;

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
                            public void complete(final String key, ResponseInfo info, JSONObject response) {
                                userInfo.setImage(key);
                                PreferenceUtil.putString(appContext, PreferenceUtil.Key.EXTRA_PHOTO_TOKEN, key);

                                //更新到后台、leancloud与友盟
                                updateSubscription = tokenModel.updateUserInfo(userInfo)
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
     *
     * @param photo_path 同时上传到友盟和leancloud上，保持同步
     */
    private void updatePhoto(String photo_path) {
        User commUser = (User) PreferenceUtil.getBean(appContext,PreferenceUtil.Key.EXTRA_COMMUSER);
        commUser.setImage(photo_path);
        PreferenceUtil.putBean(appContext,PreferenceUtil.Key.EXTRA_COMMUSER,commUser);

//        LeanchatUser leanchatUser = LeanchatUser.getCurrentUser();
//        leanchatUser.saveAvatar(photo_path, new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//
//            }
//        });

        preferenceUtils.putString(photo_path, PreferenceType.USER_PHOTO);
        editInformationView.setPhotoResult(photo_path);
        compressImage(photo_path);
    }

    private void compressImage(String imagePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //设置为true获取图片的初始大小
        opts.inJustDecodeBounds = true;
        Bitmap image = BitmapFactory.decodeFile(imagePath, opts);
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        opts.inJustDecodeBounds = false;

        //控制图片高宽中较低的一个在500像素左右
        if (Math.min(imageHeight, imageWidth) > 500) {
            float ratio = Math.max(imageHeight, imageWidth) / 500;
            opts.inSampleSize = Math.round(ratio);
        }
        Bitmap finalImage = BitmapFactory.decodeFile(imagePath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //降低画质
        finalImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);
        sdk.updateUserProtrait(finalImage, new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
            @Override
            public void onComplete(PortraitUploadResponse portraitUploadResponse) {
            }
        });
    }

    @Override
    public void updateGender(final String gender) {
        if (NetUtils.isNetworkConnected(appContext)) {
            userDetail.setGender(gender);
            updateGenderSubscription = userModel.updateUserDetail(userDetail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            editInformationView.setGender(gender);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                editInformationView.showToast(exception.getMessage());
                            } else {
                                editInformationView.showNetError();
                            }
                        }
                    });
        } else {
            editInformationView.showNetCantUse();
        }
    }

    @Override
    public void updateBirth(final String date) {
        if (NetUtils.isNetworkConnected(appContext)) {
            userDetail.setBirthday(date);
            updateBirthdaySubscription = userModel.updateUserDetail(userDetail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            editInformationView.setBirthDate(date);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                editInformationView.showToast(exception.getMessage());
                            } else {
                                editInformationView.showNetError();
                            }
                        }
                    });
        } else {
            editInformationView.showNetCantUse();
        }
    }

    @Override
    public void updateCity(final String city) {
        if (NetUtils.isNetworkConnected(appContext)) {
            userDetail.setCity(city);
            updateCitySubscription = userModel.updateUserDetail(userDetail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            editInformationView.setWorkCity(city);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                editInformationView.showToast(exception.getMessage());
                            } else {
                                editInformationView.showNetError();
                            }
                        }
                    });
        } else {
            editInformationView.showNetCantUse();
        }
    }

    @Override
    public void updateProfession(final String profession) {
        if (NetUtils.isNetworkConnected(appContext)) {
            userDetail.setProfession(profession);
            updateProfessionSubscription = userModel.updateUserDetail(userDetail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            editInformationView.setProfession(profession);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                editInformationView.showToast(exception.getMessage());
                            } else {
                                editInformationView.showNetError();
                            }
                        }
                    });
        } else {
            editInformationView.showNetCantUse();
        }
    }

    @Override
    public void init() {
        if (NetUtils.isNetworkConnected(appContext)) {
            Subscription s = preferenceUtils.getUserId()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            user_id = s;
                            userInfoSubscription = tokenModel.getUserInfo(s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfoRes>() {
                                        @Override
                                        public void call(UserInfoRes userInfoRes) {
                                            userInfo = userInfoRes;
                                            editInformationView.initUserInfo(userInfoRes);
                                        }
                                    });
                            userDetailSubscription = userModel.getUserInfoResObservable(s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfoDetailRes>() {
                                        @Override
                                        public void call(UserInfoDetailRes getUserInfoDetailRes) {
                                            TLog.i("TAG", "user info detail");
                                            userDetail = getUserInfoDetailRes;
                                            editInformationView.initDetail(userDetail);
                                        }
                                    });
                        }
                    });
        } else {
            editInformationView.showNetCantUse();
            editInformationView.initNone();
        }
    }

    @Override
    public void initDetail() {
        userDetailSubscription = userModel.getUserInfoResObservable(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfoDetailRes>() {
                    @Override
                    public void call(UserInfoDetailRes getUserInfoDetailRes) {
                        userDetail = getUserInfoDetailRes;
                        editInformationView.initDetail(userDetail);
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
        if (userInfoSubscription != null) {
            userInfoSubscription.unsubscribe();
        }
        if (userDetailSubscription != null) {
            userDetailSubscription.unsubscribe();
        }
        if (tokenSubscription != null) {
            tokenSubscription.unsubscribe();
        }
        if (updateBirthdaySubscription != null) {
            updateBirthdaySubscription.unsubscribe();
        }
        if (updateGenderSubscription != null) {
            updateGenderSubscription.unsubscribe();
        }
        if (updateProfessionSubscription != null) {
            updateProfessionSubscription.unsubscribe();
        }
        if (updateCitySubscription != null) {
            updateCitySubscription.unsubscribe();
        }
    }
}
