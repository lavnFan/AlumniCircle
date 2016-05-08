package com.seu.wufan.alumnicircle.mvp.presenter.circle;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/4/26
 */
public interface ICircleIPresenter extends IPresenter {

    void getUserTimeDynamic(String page);

    void getTopic();
}
