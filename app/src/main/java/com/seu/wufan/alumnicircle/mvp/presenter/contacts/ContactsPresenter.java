package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.api.entity.item.User;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtil;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.fragment.IContactsView;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.LeancloudProvider;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.LeanchatUser;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.UserCacheUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/10
 */
public class ContactsPresenter implements IContactsPresenter {

    Subscription preferenceSubscription;
    Subscription friendsListSubscription;
    Subscription deleteSubscription;
    IContactsView iContactsView;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private TokenModel tokenModel;
    private ContactsModel contactsModel;
    private Context appContext;

    private List<User> users = new ArrayList<>();

    @Inject
    public ContactsPresenter(@ForApplication Context context, TokenModel tokenModel, UserModel userModel, ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.contactsModel = contactsModel;
        this.tokenModel = tokenModel;
    }

    @Override
    public void initLeanCloud() {
        if (NetUtils.isNetworkConnected(appContext)) {
            preferenceSubscription = preferenceUtils.getUserId()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            if (s.isEmpty()) {

                            } else {
                                iContactsView.refreshfalse(false);
                                //设置聊天的信息
                                ChatManager.getInstance().openClient(appContext, s, new AVIMClientCallback() {
                                    @Override
                                    public void done(AVIMClient avimClient, AVIMException e) {
                                        if (null == e) {

                                        } else {
                                            iContactsView.showToast(e.toString());
                                            Log.i("leancloud", e.toString());
                                        }
                                    }
                                });
                            }
                        }
                    });
        } else {
            iContactsView.refreshfalse(false);
            iContactsView.showNetCantUse();
        }
    }

    private void getMyInfo(String s) {
        Subscription mySubscription = tokenModel.getUserInfo(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfoRes>() {
                    @Override
                    public void call(UserInfoRes userInfoRes) {
                        User user = new User();
                        user.setUser_id(userInfoRes.getUser_id());
                        user.setName(userInfoRes.getName());
                        user.setImage(userInfoRes.getImage());
                        users.add(user);

                        User savedUser = (User) PreferenceUtil.getBean(appContext, PreferenceUtil.Key.EXTRA_COMMUSER);
                        savedUser.setName(userInfoRes.getName());
                        savedUser.setImage(userInfoRes.getImage());
                        savedUser.setSchool(userInfoRes.getSchool());
                        savedUser.setMajor(userInfoRes.getMajor());
                        PreferenceUtil.putBean(appContext, PreferenceUtil.Key.EXTRA_COMMUSER, savedUser);
                    }
                });
    }

    @Override
    public void initFriendsList() {
        if (NetUtils.isNetworkConnected(appContext)) {
            preferenceSubscription = preferenceUtils.getUserId()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Subscription mySubscription = tokenModel.getUserInfo(s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfoRes>() {
                                        @Override
                                        public void call(UserInfoRes userInfoRes) {
                                            User user = new User();
                                            user.setUser_id(userInfoRes.getUser_id());
                                            user.setName(userInfoRes.getName());
                                            user.setImage(userInfoRes.getImage());
                                            users.add(user);

                                            User savedUser = (User) PreferenceUtil.getBean(appContext, PreferenceUtil.Key.EXTRA_COMMUSER);
                                            savedUser.setName(userInfoRes.getName());
                                            savedUser.setImage(userInfoRes.getImage());
                                            savedUser.setSchool(userInfoRes.getSchool());
                                            savedUser.setMajor(userInfoRes.getMajor());
                                            PreferenceUtil.putBean(appContext, PreferenceUtil.Key.EXTRA_COMMUSER, savedUser);

                                            friendsListSubscription = contactsModel.getFriendList()
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Action1<List<Friend>>() {
                                                        @Override
                                                        public void call(List<Friend> friendListItems) {
                                                            for (Friend friend : friendListItems) {
                                                                User user = new User();
                                                                user.setName(friend.getName());
                                                                user.setImage(friend.getImage());
                                                                user.setUser_id(friend.getUser_id());
                                                                users.add(user);
                                                            }
                                                            LeancloudProvider provider = new LeancloudProvider();
                                                            provider.setUsers(users);
                                                            ThirdPartUserUtils.setThirdPartUserProvider(provider);
                                                            iContactsView.initFriendsList(friendListItems);
                                                        }
                                                    }, new Action1<Throwable>() {
                                                        @Override
                                                        public void call(Throwable throwable) {
                                                            if (throwable instanceof retrofit2.HttpException) {
                                                                retrofit2.HttpException exception = (HttpException) throwable;
                                                                iContactsView.showToast(exception.getMessage());
                                                            } else {
                                                                iContactsView.showNetError();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    public void deleteFriend(String user_id) {
        if(NetUtils.isNetworkConnected(appContext)){
        deleteSubscription = contactsModel.deleteFriendReq(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        iContactsView.showToast("删除好友成功");
                        iContactsView.refreshDelete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        TLog.i("TAG",throwable.getMessage()+throwable.getCause());
                        if (throwable instanceof retrofit2.HttpException) {
                            retrofit2.HttpException exception = (HttpException) throwable;
                            iContactsView.showToast(exception.getMessage());
                        } else {
                            iContactsView.showNetError();
                        }
                    }
                });
        }else{
            iContactsView.showNetCantUse();
        }
    }

    @Override
    public void attachView(IView v) {
        iContactsView = (IContactsView) v;
    }

    @Override
    public void destroy() {
        if (preferenceSubscription != null) {
            preferenceSubscription.unsubscribe();
        }
    }
}
