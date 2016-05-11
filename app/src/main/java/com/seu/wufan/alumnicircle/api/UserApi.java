package com.seu.wufan.alumnicircle.api;


import com.seu.wufan.alumnicircle.api.entity.GetUserInfoDetailRes;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface UserApi {


    @GET("user/info/{user_id}")
    Observable<GetUserInfoDetailRes> getUserDetail(@Path("user_id")String user_id);

}
