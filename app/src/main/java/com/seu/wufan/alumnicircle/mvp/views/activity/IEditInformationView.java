package com.seu.wufan.alumnicircle.mvp.views.activity;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.views.IView;

/**
 * @author wufan
 * @date 2016/5/12
 */
public interface IEditInformationView extends IView{


    void initUserInfo(UserInfoRes res);
    void initDetail(UserInfoDetailRes res);

    void setPhotoResult(String photo_path);

}
