package com.seu.wufan.alumnicircle.mvp.presenter.me;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/11
 */
public interface IMyInformationPresenter extends IPresenter{

    void getUserDetail(String user_id);

    void getUserInfo(String user_id);

    void initUserInfo();

}
