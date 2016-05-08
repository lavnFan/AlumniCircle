package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.TokenApi;
import com.seu.wufan.alumnicircle.api.entity.LoginReq;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
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

    public  Observable<LoginRes> register(String phone_num,String enroll_year,String school,String major,String password,String name){
        RegisterReq req = new RegisterReq();
        req.setPhone_num(phone_num);
        req.setEnroll_year(enroll_year);
        req.setSchool(school);
        req.setMajor(major);
        req.setPassword(password);
        req.setName(name);
        return getService().register(req);
    }

}
