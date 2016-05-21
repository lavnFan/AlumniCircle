package com.seu.wufan.alumnicircle.mvp.views.activity.me;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;

/**
 * @author wufan
 * @date 2016/5/11
 */
public interface IMyInformationView extends IView{

    void initMyInfo(UserInfoDetailRes res);

    void initMyInfo(UserInfoRes res);

    void initDynamic(FeedItem feedItem,boolean noEmpty);

    void goDynamic(CommUser user);

    void sendMsg(String other_id);

    void sendFriendMsg(String other_id,String name);

    void hideSendBtn();

    void setBtnMsg();
}
