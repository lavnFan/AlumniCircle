package com.seu.wufan.alumnicircle.presenter;

import android.view.View;

import com.seu.wufan.alumnicircle.api.entity.LoginReq;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface ILoginPresenter extends Presenter{

    void doLogin(String username, String password);

    boolean isValid(String username, String password);

}
