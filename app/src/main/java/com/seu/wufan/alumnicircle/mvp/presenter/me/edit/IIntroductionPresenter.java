package com.seu.wufan.alumnicircle.mvp.presenter.me.edit;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/14
 */
public interface IIntroductionPresenter extends IPresenter{

    void updateIntroduction(UserInfoDetailRes userInfoDetailRes);

}
