package com.seu.wufan.alumnicircle.api;

import com.seu.wufan.alumnicircle.api.entity.LoginReq;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.QnReq;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.WeixinReq;
import com.seu.wufan.alumnicircle.api.entity.item.TokenRes;

import java.util.List;

import butterknife.Bind;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface TokenApi {

    //登录
    @POST("auth/login/")
    Observable<LoginRes> login(@Body LoginReq req);

    //获取用户信息
    @GET("user/{user_id}")
    Observable<UserInfoRes> getUserInfo(@Path("user_id")String user_id);

    //注册
    @POST("user/")
    Observable<LoginRes> register(@Body RegisterReq req);

    //创建七牛上传凭证
    @POST("static/token/")
    Observable<List<QnRes>> createQiNiuToken(@Body QnReq req);

    @PUT("user/")
    Observable<Void> updateUserInfo(@Body UserInfoRes req);

    @POST("auth/weixin/login/")
    Observable<TokenRes> loginByWeiXin(@Body WeixinReq req);

}
