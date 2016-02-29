package com.seu.wufan.alumnicircle.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.TokenApi;
import com.seu.wufan.alumnicircle.api.entity.LoginReq;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.common.base.BaseModel;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import rx.Observable;

/**
 * @author wufan
 * @date 2016/2/29
 */
public class TokenModel extends BaseModel<TokenApi> {

    public TokenModel(@Nullable TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    @Override
    protected Class<TokenApi> getServiceClass() {
        return TokenApi.class;
    }

    public Observable<LoginRes> login(String phoneNum,String password){
        LoginReq loginReq = new LoginReq();
        loginReq.setPhone_num(phoneNum);
        loginReq.setPassword(password);
        return getService().login(loginReq);
    }

}
