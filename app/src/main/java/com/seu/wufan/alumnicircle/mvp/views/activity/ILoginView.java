package com.seu.wufan.alumnicircle.mvp.views.activity;

import com.seu.wufan.alumnicircle.mvp.views.IView;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface ILoginView extends IView{

    String getUser_id();

    void loginSuccess();

}
