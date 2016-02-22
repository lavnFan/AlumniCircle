package com.seu.wufan.alumnicircle.api;


import com.seu.wufan.alumnicircle.model.api.LoginReq;
import com.seu.wufan.alumnicircle.model.api.LoginRes;
import com.seu.wufan.alumnicircle.model.api.QnRes;
import com.seu.wufan.alumnicircle.model.api.RegisterReq;
import com.seu.wufan.alumnicircle.model.api.UserInfoRes;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @author wufan
 * @date 2016/2/20
 */
public interface AlumniCircleService {

    //创建七牛上传凭证
    @POST("/static/token/")
    void createQiNiuToken(Callback<QnRes> cb);

    //注册
    @POST("/user/")
    void register(@Body RegisterReq req,Callback<LoginRes> cb);

    //登录
    @POST("/auth/login/")
    void login(@Body LoginReq req, Callback<LoginRes> cb);

    //获取用户信息
    @GET("/user/{user_id}")
    void getUserInfo(@Path("user_id")String user_id,Callback<UserInfoRes> cb);


}
