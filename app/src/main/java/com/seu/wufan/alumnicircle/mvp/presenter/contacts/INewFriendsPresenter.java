package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.widget.Button;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/20
 */
public interface INewFriendsPresenter extends IPresenter {

    void init();

    void acceptFriendRequest(String user_id,Button sendBtn);

    void deleteFriendRequest(String user_id);

}
