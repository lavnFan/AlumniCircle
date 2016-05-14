package com.seu.wufan.alumnicircle.api;


import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface UserApi {


    @GET("user/info/{user_id}")
    Observable<UserInfoDetailRes> getUserDetail(@Path("user_id")String user_id);

    @PUT("user/info")
    Observable<Void> updateUserDetail(@Body UserInfoDetailRes req);

}
