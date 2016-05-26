package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.TokenApi;
import com.seu.wufan.alumnicircle.api.entity.LoginReq;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.QnReq;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.WeixinReq;
import com.seu.wufan.alumnicircle.api.entity.item.TokenRes;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import java.util.List;

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

    public  Observable<LoginRes> register(String phone_num,String enroll_year,String school,String major,String password,String name,String number){
        RegisterReq req = new RegisterReq();
        req.setPhone_num(phone_num);
        req.setEnroll_year(enroll_year);
        req.setSchool(school);
        req.setMajor(major);
        req.setPassword(password);
        req.setName(name);
        req.setStudent_num(number);
        return getService().register(req);
    }

    public Observable<UserInfoRes> getUserInfo(String user_id){
        return getService().getUserInfo(user_id);
    }

    public Observable<List<QnRes>> createQiNiuToken(int count){
        QnReq req = new QnReq();
        req.setCount(count);
        return getService().createQiNiuToken(req);
    }

    public Observable<Void> updateUserInfo(UserInfoRes req){
        return getService().updateUserInfo(req);
    }

    public Observable<TokenRes> loginByWeiXin(String access_token, String open_id){
        WeixinReq req = new WeixinReq();
        req.setAccess_token(access_token);
        req.setOpen_id(open_id);
        return getService().loginByWeiXin(req);
    }

}
