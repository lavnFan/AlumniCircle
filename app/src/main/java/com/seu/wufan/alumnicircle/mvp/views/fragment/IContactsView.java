package com.seu.wufan.alumnicircle.mvp.views.fragment;

import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.mvp.views.IView;

import java.util.List;

/**
 * @author wufan
 * @date 2016/5/10
 */
public interface IContactsView extends IView{


    void initFriendsList(List<Friend> friendListItems);

    void refreshfalse(boolean b);

    void refreshDelete();
}
