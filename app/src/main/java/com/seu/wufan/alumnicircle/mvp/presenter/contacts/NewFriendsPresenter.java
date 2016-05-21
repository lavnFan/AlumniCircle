package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.content.Context;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.INewFriendsView;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.AddRequest;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.AddRequestManager;

import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/20
 */
public class NewFriendsPresenter implements INewFriendsPresenter {

    private Subscription getRequestSubscription;
    private Subscription acceptSubscription;
    private Subscription deleteSubscription;
    private INewFriendsView iNewFriendsView;
    private PreferenceUtils preferenceUtils;
    private ContactsModel contactsModel;
    private Context appContext;

    @Inject
    public NewFriendsPresenter(@ForApplication Context context, ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.contactsModel = contactsModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void attachView(IView v) {
        iNewFriendsView = (INewFriendsView) v;
    }

    @Override
    public void destroy() {
        if (getRequestSubscription != null) {
            getRequestSubscription.unsubscribe();
        }
    }

    @Override
    public void init() {
        if (NetUtils.isNetworkConnected(appContext)) {
            getRequestSubscription = contactsModel.getFriendRequestList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<FriendRequestItem>>() {
                        @Override
                        public void call(List<FriendRequestItem> friendRequestItems) {
                            iNewFriendsView.init(friendRequestItems);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iNewFriendsView.showToast(exception.getMessage());
                            } else {
                                iNewFriendsView.showNetError();
                            }
                        }
                    });
        } else {
            iNewFriendsView.showNetCantUse();
        }
    }

    @Override
    public void acceptFriendRequest(final String user_id, final Button sendBtn) {
        if (NetUtils.isNetworkConnected(appContext)) {
            acceptSubscription = contactsModel.acceptFriendReq(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            iNewFriendsView.acceptSuccess(sendBtn);
                            //同时更新到leancloud中
//                            AddRequest addRequest = new AddRequest();
//                            addRequest.setObjectId(user_id);
//                            agreeAddRequest(addRequest);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iNewFriendsView.showToast(exception.getMessage());
                            } else {
                                iNewFriendsView.showNetError();
                            }
                        }
                    });
        }else{
            iNewFriendsView.showNetCantUse();
        }
    }

    @Override
    public void deleteFriendRequest(String user_id) {
        if (NetUtils.isNetworkConnected(appContext)) {
            deleteSubscription = contactsModel.deleteFriendReq(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            iNewFriendsView.deleteSuccess();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iNewFriendsView.showToast(exception.getMessage());
                            } else {
                                iNewFriendsView.showNetError();
                            }
                        }
                    });
        }else{
            iNewFriendsView.showNetCantUse();
        }
    }

    private void agreeAddRequest(final AddRequest addRequest) {
//        final ProgressDialog dialog = showSpinnerDialog();
        AddRequestManager.getInstance().agreeAddRequest(addRequest, new SaveCallback() {
            @Override
            public void done(AVException e) {
//                dialog.dismiss();
//                if (filterException(e)) {
                    if (addRequest.getFromUser() != null) {
                        sendWelcomeMessage(addRequest.getFromUser().getObjectId());
                    }
//                    loadMoreAddRequest(false);
//                    ContactRefreshEvent event = new ContactRefreshEvent();
//                    EventBus.getDefault().post(event);
//                }
            }
        });
    }

    public void sendWelcomeMessage(String toUserId) {
        ChatManager.getInstance().createSingleConversation(toUserId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
                if (e == null) {
                    AVIMTextMessage message = new AVIMTextMessage();
                    message.setText("我们已经是好友了，来聊天吧");
                    avimConversation.sendMessage(message, null);
                }
            }
        });
    }
}
