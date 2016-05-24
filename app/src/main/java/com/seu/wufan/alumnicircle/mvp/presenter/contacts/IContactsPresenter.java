package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/10
 */
public interface IContactsPresenter extends IPresenter{

    void initLeanCloud();

    void initFriendsList();

    void deleteFriend(String user_id);
}
