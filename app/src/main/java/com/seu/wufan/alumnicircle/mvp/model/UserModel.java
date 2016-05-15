package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.UserApi;
import com.seu.wufan.alumnicircle.api.entity.EduRes;
import com.seu.wufan.alumnicircle.api.entity.JobRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
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

    public Observable<UserInfoDetailRes> getUserInfoResObservable(String user_id) {
        return getService().getUserDetail(user_id);
    }

    public Observable<Void> updateUserDetail(UserInfoDetailRes req) {
        return getService().updateUserDetail(req);
    }

    public Observable<EduRes> addEduHistory(Edu req) {
        return getService().addEduHistory(req);
    }

    public Observable<Void> updateEduHistory(String id, Edu req) {
        return getService().updateEduHistory(id, req);
    }

    public Observable<Void> deleteEduHistory(String id) {
        return getService().deleteEduHistory(id);
    }

    public Observable<JobRes> addJobHistory(Job req) {
        return getService().addJobHistory(req);
    }

    public Observable<Void> updateJobHistory(String id, Job req) {
        return getService().updateJobHistory(id, req);
    }

    public Observable<Void> deleteJobHistory(String id) {
        return getService().deleteJobHistory(id);
    }
}
