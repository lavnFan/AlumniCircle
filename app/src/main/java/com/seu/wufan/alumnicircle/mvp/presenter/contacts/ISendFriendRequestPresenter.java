package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import com.seu.wufan.alumnicircle.api.entity.FriendReq;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/20
 */
public interface ISendFriendRequestPresenter extends IPresenter{

    void sendFriendRequest(String other_id, FriendReq req);

}
