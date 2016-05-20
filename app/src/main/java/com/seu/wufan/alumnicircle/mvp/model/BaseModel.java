package com.seu.wufan.alumnicircle.mvp.model;

import android.database.Observable;
import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.BuildConfig;
import com.seu.wufan.alumnicircle.api.RequestIntercepterImpl;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * @author wufan
 * @date 2016/2/29
 */
public abstract class BaseModel<T>{
    private T service;

    private RequestIntercepterImpl requestIntercepter;

    @Nullable
    private TokenProvider tokenProvider;

    protected abstract Class<T> getServiceClass();

    public BaseModel(@Nullable TokenProvider tokenProvider){
        requestIntercepter = new RequestIntercepterImpl();

        if(tokenProvider != null){
            this.tokenProvider = tokenProvider;
            requestIntercepter.setTokenProvider(tokenProvider);
            setTokenProvider(tokenProvider);
        }
    }

    public void setTokenProvider(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
        requestIntercepter.setTokenProvider(tokenProvider);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(requestIntercepter)
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BuildConfig.ENDPOINT)
                .client(okHttpClient)
                .build();

        service = retrofit.create(getServiceClass());
    }

    public T getService() {
        return service;
    }


}
