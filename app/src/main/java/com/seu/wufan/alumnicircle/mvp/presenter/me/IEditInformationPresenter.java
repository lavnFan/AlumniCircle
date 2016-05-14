package com.seu.wufan.alumnicircle.mvp.presenter.me;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/12
 */
public interface IEditInformationPresenter extends IPresenter{

    void savePhoto(String photo_path);

    void init();

    UserInfoRes getUserInfo();

    UserInfoDetailRes getUserDetail();

}
