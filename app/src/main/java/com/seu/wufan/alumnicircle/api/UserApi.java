package com.seu.wufan.alumnicircle.api;


import com.seu.wufan.alumnicircle.api.entity.EduRes;
import com.seu.wufan.alumnicircle.api.entity.JobRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Job;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
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


    @POST("user/info/eduHistory")
    Observable<EduRes> addEduHistory(@Body Edu req);

    @PUT("user/info/eduHistory/{id}")
    Observable<Void> updateEduHistory(@Path("id")String id,@Body Edu req);

    @DELETE("user/info/eduHistory/{id}")
    Observable<Void> deleteEduHistory(@Path("id")String id);

    @POST("user/info/jobHistory")
    Observable<JobRes> addJobHistory(@Body Job req);

    @PUT("user/info/jobHistory/{id}")
    Observable<Void> updateJobHistory(@Path("id")String id,@Body Job req);

    @DELETE("user/info/jobHistory/{id}")
    Observable<Void> deleteJobHistory(@Path("id")String id);
}
