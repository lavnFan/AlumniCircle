package com.seu.wufan.alumnicircle.mvp.presenter.impl.login;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface ILoginIPresenter extends IPresenter {

    void doLogin(String username, String password);

    boolean isValid(String username, String password);

}
