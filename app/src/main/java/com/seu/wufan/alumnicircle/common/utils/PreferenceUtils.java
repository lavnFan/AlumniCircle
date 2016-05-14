package com.seu.wufan.alumnicircle.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * SharedPreferences工具类
 *
 * @author wangdd
 * @date 2015-2-1
 */
public class PreferenceUtils {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String APP = "com.icon_seu.wufan.alumnicircle";

    public PreferenceUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Observable<String> getUserId() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String user_id = sharedPreferences.getString(PreferenceType.USER_ID, "");
                subscriber.onStart();
                subscriber.onNext(user_id);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getPhone() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String phone = sharedPreferences.getString(PreferenceType.PHONE, "");
                subscriber.onStart();
                subscriber.onNext(phone);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getAccessToken() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String access = sharedPreferences.getString(PreferenceType.ACCESS_TOKEN, "");
                subscriber.onStart();
                subscriber.onNext(access);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getUserPhoto() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String photoPath = sharedPreferences.getString(PreferenceType.USER_PHOTO, "");
                subscriber.onStart();
                subscriber.onNext(photoPath);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getUserName() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String name = sharedPreferences.getString(PreferenceType.USER_NAME, "");
                subscriber.onStart();
                subscriber.onNext(name);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void putString(final String s, @PreferenceType final String type) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                editor.putString(type, s);
                editor.commit();
                subscriber.onStart();
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                });
    }




}