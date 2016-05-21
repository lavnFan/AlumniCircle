package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;

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
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IMyInformationView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.ProfileResponse;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import retrofit2.http.Path;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/11
 */
public class MyInformationPresenter implements IMyInformationPresenter {

    private Subscription infoSubscription;
    private Subscription detailSubscription;
    private Subscription friendListSubscription;
    private Subscription judgeFriendSubscription;

    private IMyInformationView iMyInformationView;
    private PreferenceUtils preferenceUtils;
    private UserModel userModel;
    private TokenModel tokenModel;
    private ContactsModel contactsModel;
    private Context appContext;

    CommUser commUser = new CommUser();
    private boolean isFriend = false;

    @Inject
    public MyInformationPresenter(@ForApplication Context context, UserModel userModel, TokenModel tokenModel, ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.userModel = userModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
        this.tokenModel = tokenModel;
        this.contactsModel = contactsModel;
    }

    @Override
    public void initUser(final String user_id) {
        judgeFriendSubscription = preferenceUtils.getUserId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s.equals(user_id)) {              //如果是自身，则隐藏
                            iMyInformationView.hideSendBtn();
                        } else {                               //获取好友列表，再进行比较
                            friendListSubscription = contactsModel.getFriendList()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<Friend>>() {
                                        @Override
                                        public void call(List<Friend> friendListItems) {
                                            Friend item = new Friend();
                                            for (Friend friendListItem : friendListItems) {
                                                if (user_id.equals(friendListItem.getUser_id())) {
                                                    isFriend = true;
                                                    break;
                                                }
                                            }
                                            if (isFriend) {
                                                iMyInformationView.setBtnMsg();      //显示发消息
                                            }
                                        }
                                    });
                        }
                    }
                });

        if (NetUtils.isNetworkConnected(appContext)) {


            detailSubscription = userModel.getUserInfoResObservable(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<UserInfoDetailRes>() {
                        @Override
                        public void call(UserInfoDetailRes userInfoDetailRes) {
                            iMyInformationView.initMyInfo(userInfoDetailRes);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iMyInformationView.showToast(exception.getMessage());
                            } else {
                                iMyInformationView.showNetError();
                            }
                        }
                    });
            infoSubscription = tokenModel.getUserInfo(user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<UserInfoRes>() {
                        @Override
                        public void call(UserInfoRes userInfoRes) {
                            iMyInformationView.initMyInfo(userInfoRes);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iMyInformationView.showToast(exception.getMessage());
                            } else {
                                iMyInformationView.showNetError();
                            }
                        }
                    });

            CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);
            //获取友盟id
            sdk.fetchUserProfile(user_id, "self_account", new Listeners.FetchListener<ProfileResponse>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(ProfileResponse profileResponse) {
                    commUser = profileResponse.result;
                    getUmengDynamic(commUser.id);
                }
            });


        } else {
            iMyInformationView.showNetCantUse();
        }
    }

    private void getUmengDynamic(String id) {
        CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);
        sdk.fetchUserTimeLine(id, new Listeners.FetchListener<FeedsResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(FeedsResponse feedsResponse) {
                initDynamic(feedsResponse.result);
            }
        });

    }

    @Override
    public void getCommUser(String user_id) {
        iMyInformationView.goDynamic(commUser);
    }

    @Override
    public void sendMsg(final String other_id) {
        //1、若不是好友，则显示加好友
        //2、若是好友，则显示发信息
        if (NetUtils.isNetworkConnected(appContext)) {
            if (isFriend) {
                iMyInformationView.sendMsg(other_id);       //跳到会话界面
            } else {
                Subscription nameSubscription = preferenceUtils.getUserName()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                iMyInformationView.sendFriendMsg(other_id, s);   //跳到发送验证信息界面
                            }
                        });
            }
        } else {
            iMyInformationView.showNetCantUse();
        }
    }

    private void initDynamic(List<FeedItem> result) {
        if (result.isEmpty()) {
            iMyInformationView.initDynamic(null, false);
        } else {
            iMyInformationView.initDynamic(result.get(0), true);
        }

    }

    @Override
    public void attachView(IView v) {
        iMyInformationView = (IMyInformationView) v;
    }

    @Override
    public void destroy() {
        if (infoSubscription != null) {
            infoSubscription.unsubscribe();
        }
        if (detailSubscription != null) {
            detailSubscription.unsubscribe();
        }
        if (friendListSubscription != null) {
            friendListSubscription.unsubscribe();
        }
    }
}
