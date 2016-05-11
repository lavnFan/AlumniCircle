package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.UserApi;
import com.seu.wufan.alumnicircle.api.entity.GetUserInfoDetailRes;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import rx.Observable;

/**
 * @author wufan
 * @date 2016/2/29
 */
public class UserModel extends BaseModel<UserApi> {

    public UserModel(@Nullable TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    @Override
    protected Class<UserApi> getServiceClass() {
        return UserApi.class;
    }


    public Observable<GetUserInfoDetailRes> getUserInfoResObservable(String user_id){
        return getService().getUserDetail(user_id);
    }

}
