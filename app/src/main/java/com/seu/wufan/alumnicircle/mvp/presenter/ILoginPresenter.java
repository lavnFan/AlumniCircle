package com.seu.wufan.alumnicircle.mvp.presenter;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface ILoginPresenter extends Presenter{

    void doLogin(String username, String password);

    boolean isValid(String username, String password);

}
