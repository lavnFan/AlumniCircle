package com.seu.wufan.alumnicircle.mvp.presenter;

import com.seu.wufan.alumnicircle.api.entity.RegisterReq;

/**
 * @author wufan
 * @date 2016/3/1
 */
public interface IRegisterPresenter extends Presenter {

    void doRegister(String phone_num,String password,String enroll_year,String school,String major);

    boolean isValid(String phone_num,String password,String enroll_year,String school,String major);
}
