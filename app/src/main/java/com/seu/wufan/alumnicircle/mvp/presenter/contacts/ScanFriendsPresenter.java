package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.IScanFriendsView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISearchView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author wufan
 * @date 2016/5/24
 */
public class ScanFriendsPresenter implements IScanFriendsPresenter{

    private Subscription addFriendSubscription;
    private IScanFriendsView iScanFriendsView;
    private PreferenceUtils preferenceUtils;
    private ContactsModel contactsModel;
    private Context appContext;

    @Inject
    public ScanFriendsPresenter(@ForApplication Context context, ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.contactsModel = contactsModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void addFriend(String user_id) {
//        addFriendSubscription = contactsModel.sendFriendRequest(user_id,)
    }

    @Override
    public void attachView(IView v) {
        iScanFriendsView = (IScanFriendsView) v;
    }

    @Override
    public void destroy() {
        if(addFriendSubscription !=null){
            addFriendSubscription.unsubscribe();
        }
    }
}
