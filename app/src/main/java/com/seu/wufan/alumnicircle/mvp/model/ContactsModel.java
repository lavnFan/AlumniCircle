package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.ContactsApi;
import com.seu.wufan.alumnicircle.api.entity.FriendReq;
import com.seu.wufan.alumnicircle.api.entity.PublishDynamicReq;
import com.seu.wufan.alumnicircle.api.entity.item.DeleteBody;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.api.entity.item.SearchFriendItem;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import java.util.List;

import rx.Observable;

/**
 * @author wufan
 * @date 2016/4/24
 */
public class ContactsModel extends BaseModel<ContactsApi>{

    public ContactsModel(@Nullable TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    @Override
    protected Class<ContactsApi> getServiceClass() {
        return ContactsApi.class;
    }

    public Observable<List<Friend>> getFriendList(){
        return getService().getFriendList();
    }

    public Observable<Void> deleteFriend(String user_id){
        return getService().deleteFriend(user_id);
    }

    public Observable<List<FriendRequestItem>> getFriendRequestList(){
        return getService().getFriendRequestList();
    }

    public Observable<Void> sendFriendRequest(String user_id, FriendReq req){
        return getService().sendFriendRequest(user_id,req);
    }

    public Observable<Void> acceptFriendReq(String user_id){
        return getService().acceptFriendReq(user_id);
    }

    public Observable<Void> deleteFriendReq(String user_id){
        return getService().deleteFriendReq(user_id);
    }

    public Observable<List<SearchFriendItem>> searchUser(String name,String student_id){
        return getService().searchUser(name,student_id);
    }
}
