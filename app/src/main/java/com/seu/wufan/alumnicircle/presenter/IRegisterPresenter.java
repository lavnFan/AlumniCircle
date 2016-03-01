package com.seu.wufan.alumnicircle.presenter;

import com.seu.wufan.alumnicircle.api.entity.RegisterReq;

/**
 * @author wufan
 * @date 2016/3/1
 */
public interface IRegisterPresenter extends Presenter {

    void doRegister(RegisterReq req);

    void isValid(RegisterReq req);
}
