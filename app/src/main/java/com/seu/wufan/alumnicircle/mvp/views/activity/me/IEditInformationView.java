package com.seu.wufan.alumnicircle.mvp.views.activity.me;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.views.IView;

/**
 * @author wufan
 * @date 2016/5/12
 */
public interface IEditInformationView extends IView{

    void initNone();

    void initUserInfo(UserInfoRes res);

    void initDetail(UserInfoDetailRes res);

    void setPhotoResult(String photo_path);

    void setGender(String gender);

    void setBirthDate(String date);

    void setWorkCity(String city);

    void setProfession(String profession);


}
