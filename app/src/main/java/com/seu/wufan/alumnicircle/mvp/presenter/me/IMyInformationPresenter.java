package com.seu.wufan.alumnicircle.mvp.presenter.me;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;
import com.umeng.comm.core.beans.CommUser;

/**
 * @author wufan
 * @date 2016/5/11
 */
public interface IMyInformationPresenter extends IPresenter{

    void initUser(String user_id);

    void getCommUser(String user_id);

    void sendMsg(String other_id);

}
