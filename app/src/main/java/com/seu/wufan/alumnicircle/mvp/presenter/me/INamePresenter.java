package com.seu.wufan.alumnicircle.mvp.presenter.me;

import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/13
 */
public interface INamePresenter extends IPresenter{

    void updateName(UserInfoRes userInfoRes);

}
