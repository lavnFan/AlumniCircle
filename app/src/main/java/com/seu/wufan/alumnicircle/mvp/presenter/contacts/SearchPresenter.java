package com.seu.wufan.alumnicircle.mvp.presenter.contacts;

import android.content.Context;

import com.seu.wufan.alumnicircle.api.entity.item.SearchFriendItem;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.ContactsModel;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISearchView;

import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author wufan
 * @date 2016/5/22
 */
public class SearchPresenter implements ISearchPresenter {

    private Subscription userSubscription;
    private ISearchView iSearchView;
    private PreferenceUtils preferenceUtils;
    private ContactsModel contactsModel;
    private Context appContext;

    @Inject
    public SearchPresenter(@ForApplication Context context, ContactsModel contactsModel, PreferenceUtils preferenceUtils) {
        this.contactsModel = contactsModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void attachView(IView v) {
        iSearchView = (ISearchView) v;
    }

    @Override
    public void destroy() {
        if(userSubscription!=null){
            userSubscription.unsubscribe();
        }
    }

    @Override
    public void search(String keyWord) {
        if (NetUtils.isNetworkConnected(appContext)) {
            userSubscription = contactsModel.searchUser(keyWord,keyWord)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<SearchFriendItem>>() {
                        @Override
                        public void call(List<SearchFriendItem> searchFriendItems) {
                            iSearchView.showUserResult(searchFriendItems);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof retrofit2.HttpException) {
                                retrofit2.HttpException exception = (HttpException) throwable;
                                iSearchView.showToast(exception.getMessage());
                            } else {
                                iSearchView.showNetError();
                            }
                        }
                    });
        }else {
            iSearchView.showNetCantUse();
        }
    }
}
