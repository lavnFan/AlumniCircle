package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.FriendReq;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISendFriendRequestView;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.decode.Constants;

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
public class SendFriendRequestPresenter implements ISendFriendRequestPresenter{

    private Subscription subscription;
    private ISendFriendRequestView iSendFriendRequestView;
    private PreferenceUtils preferenceUtils;
    private ContactsModel contactsModel;
    private Context appContext;

    @Inject
    public SendFriendRequestPresenter(@ForApplication Context context, ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.contactsModel = contactsModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }


    @Override
    public void sendFriendRequest(String other_id, FriendReq req) {
        if(NetUtils.isNetworkConnected(appContext)){
            subscription = contactsModel.sendFriendRequest(other_id,req)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            iSendFriendRequestView.sendRequstSuccess();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iSendFriendRequestView.showToast(exception.getMessage());
                            } else {
                                iSendFriendRequestView.showNetError();
                            }
                        }
                    });
        }else {
            iSendFriendRequestView.showNetCantUse();
        }
    }

    @Override
    public void attachView(IView v) {
        iSendFriendRequestView= (ISendFriendRequestView) v;
    }

    @Override
    public void destroy() {
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
}
