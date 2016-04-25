package com.seu.wufan.alumnicircle.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.orhanobut.logger.Logger;
import com.seu.wufan.alumnicircle.common.qualifier.PreferenceType;

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

    public PreferenceUtils(Context context){
        sharedPreferences = context.getSharedPreferences(APP,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Observable<String> getUserId(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String user_id = sharedPreferences.getString(PreferenceType.USER_ID,"");
                subscriber.onStart();
                subscriber.onNext(user_id);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getPhone(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String phone = sharedPreferences.getString(PreferenceType.PHONE,"");
                subscriber.onStart();
                subscriber.onNext(phone);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getAccessToken(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String access = sharedPreferences.getString(PreferenceType.ACCESS_TOKEN,"");
                Logger.d(access.length() + "");
                subscriber.onStart();
                subscriber.onNext(access);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void putString(final String s,@PreferenceType final String type){
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                editor.putString(type,s);
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

//    public static class Key {
//        public static final String USER_ID = "USER_ID";
//        public static final String ACCESS="ACCESS_token";
//        public static final String PHONE="PHONE";
//        public static final String LOCATION="location";
//        public static final String ENROLL_YEAR="ENROLL_YEAR";
//        public static final String NAME = "NAME";
//        public static final String FIRST_USE = "FIRST_USE";
//        public static final String LOGIN = "LOGIN";
//        public static final String VERSION_CODE = "VERSION_CODE";
//        public static final String PHOTO_PATH="PHOTO_Path";
//    }
//
//    public static final String DEFAULT_STRING = "";
//    public static final int DEFAULT_INT = 0;
//    public static final boolean DEFAULT_BOOLEAN = false;
//    public static final long DEFAULT_LONG = 0;
//    public static final float DEFAULT_FLOAT = 0.0f;
//
//
//    public static String getString(Context context, String key) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        return settings.getString(key, DEFAULT_STRING);
//    }
//    public static String getString(Context context, String key,String defaultvalue) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        return settings.getString(key, defaultvalue);
//    }
//
//    public static void putString(Context context, final String key,
//                                 final String value) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        settings.edit().putString(key, value).commit();
//    }
//
//    public static boolean getBoolean(Context context, final String key) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        return settings.getBoolean(key, DEFAULT_BOOLEAN);
//    }
//
//    public static boolean hasKey(Context context, final String key) {
//        return PreferenceManager.getDefaultSharedPreferences(context).contains(
//                key);
//    }
//
//    public static void putBoolean(Context context, final String key,
//                                  final boolean value) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        settings.edit().putBoolean(key, value).commit();
//    }
//
//    public static void putInt(Context context, final String key,
//                              final int value) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        settings.edit().putInt(key, value).commit();
//    }
//
//    public static int getInt(Context context, final String key) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        return settings.getInt(key, DEFAULT_INT);
//    }
//
//    public static void putFloat(Context context, final String key,
//                                final float value) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        settings.edit().putFloat(key, value).commit();
//    }
//
//    public static float getFloat(Context context, final String key) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        return settings.getFloat(key, DEFAULT_FLOAT);
//    }
//
//    public static void putLong(Context context, final String key,
//                               final long value) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        settings.edit().putLong(key, value).commit();
//    }
//
//    public static long getLong(Context context, final String key) {
//        final SharedPreferences settings = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        return settings.getLong(key, DEFAULT_LONG);
//    }
//
//    public static void clearPreference(Context context,
//                                       final SharedPreferences p) {
//        final Editor editor = p.edit();
//        editor.clear();
//        editor.commit();
//    }
}
