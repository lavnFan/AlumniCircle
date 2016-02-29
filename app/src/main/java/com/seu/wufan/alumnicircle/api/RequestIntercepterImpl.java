package com.seu.wufan.alumnicircle.api;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestIntercepterImpl implements Interceptor {
    @Nullable
    private TokenProvider tokenProvider;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(tokenProvider != null){
            Request request = chain.request().newBuilder().addHeader("Access_token",getToken()).build();
            return chain.proceed(request);
        }
        return null;
    }

    public void setTokenProvider(@Nullable TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String getToken(){
        if(tokenProvider != null){
            return tokenProvider.getToken();
        }
        return null;
    }
}
