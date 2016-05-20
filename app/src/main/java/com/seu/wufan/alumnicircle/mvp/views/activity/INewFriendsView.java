package com.seu.wufan.alumnicircle.mvp.views.activity;

import android.widget.Button;

import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.mvp.views.IView;

import java.util.List;

/**
 * @author wufan
 * @date 2016/5/20
 */
public interface INewFriendsView extends IView{


    void init(List<FriendRequestItem> friendRequestItems);

    void deleteSuccess();

    void acceptSuccess(Button sendBtn);

}
