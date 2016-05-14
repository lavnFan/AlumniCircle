package com.seu.wufan.alumnicircle.mvp.views.activity;

import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.umeng.comm.core.beans.CommUser;

/**
 * @author wufan
 * @date 2016/5/11
 */
public interface IMyView extends IView{

    void setUser(CommUser user);

    void setName(String name);

    void setPhoto(String photo_path);

}
