package com.seu.wufan.alumnicircle.mvp.presenter.login;

import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/3/1
 */
public interface IRegisterIPresenter extends IPresenter {

    void doRegister(String phone_num,String password,String enroll_year,String school,String major,String name,String number);

    boolean isValid(String phone_num,String password,String enroll_year,String school,String major,String name,String number);

    void doWeixin(UserInfoRes userInfoRes);
}
