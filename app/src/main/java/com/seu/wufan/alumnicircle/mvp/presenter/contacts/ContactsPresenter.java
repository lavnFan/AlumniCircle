package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.model.UserModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.fragment.IContactsView;

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
    IContactsView iContactsView;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private ContactsModel contactsModel;
    private Context appContext;

    @Inject
    public ContactsPresenter(@ForApplication Context context, UserModel userModel,ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.contactsModel = contactsModel;
    }

    @Override
    public void initLeanCloud() {
        preferenceSubscription = preferenceUtils.getUserId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(s.isEmpty()){

                        }else {
                            //设置聊天的信息
                            ChatManager.getInstance().openClient(appContext, s, new AVIMClientCallback() {
                                @Override
                                public void done(AVIMClient avimClient, AVIMException e) {
                                    if (null == e) {

                                    } else {
                                        iContactsView.showToast(e.toString());
                                        Log.i("leancloud",e.toString());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void initFriendsList() {
        if(NetUtils.isNetworkConnected(appContext)){
            friendsListSubscription = contactsModel.getFriendList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Friend>>() {
                        @Override
                        public void call(List<Friend> friendListItems) {
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
        }else {
            iContactsView.showNetCantUse();
        }
    }

    @Override
    public void attachView(IView v) {
        iContactsView = (IContactsView) v;
    }

    @Override
    public void destroy() {
        if(preferenceSubscription!=null){
            preferenceSubscription.unsubscribe();
        }
    }
}
