package com.seu.wufan.alumnicircle.presenter;


import com.seu.wufan.alumnicircle.ui.views.IView;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface Presenter {
    void attachView(IView v);

    void destroy();
}
