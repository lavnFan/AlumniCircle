package com.seu.wufan.alumnicircle.api;


import com.seu.wufan.alumnicircle.api.entity.FriendReq;
import com.seu.wufan.alumnicircle.api.entity.item.DeleteBody;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.api.entity.item.SearchFriendItem;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import retrofit.http.RestMethod;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author wufan
 * @date 2016/4/24
 */
public interface ContactsApi {

    @GET("friends/")
    Observable<List<Friend>> getFriendList();

    @DELETE("friends/{user_id}")
    Observable<Void> deleteFriend(@Path("user_id")String user_id);

    @GET("friends/request")
    Observable<List<FriendRequestItem>> getFriendRequestList();

    @PUT("friends/{user_id}")
    Observable<Void> sendFriendRequest(@Path("user_id")String user_id, @Body FriendReq req);

    @PUT("friends/accept/{user_id}")
    Observable<Void> acceptFriendReq(@Path("user_id")String user_id);

    @DELETE("friends/accept/{user_id}")
    Observable<Void> deleteFriendReq(@Path("user_id")String user_id);

    @GET("user/search")
    Observable<List<SearchFriendItem>> searchUser(@Query("name")String name,@Query("student_id")String student_id);

}
