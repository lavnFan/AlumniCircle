package com.seu.wufan.alumnicircle.ui.views;

import android.support.annotation.NonNull;

/**
 * @author wufan
 * @date 2016/2/29
 */
public interface IView {
    void showNetCantUse();

    void showNetError();

    void showToast(@NonNull String s);

}
