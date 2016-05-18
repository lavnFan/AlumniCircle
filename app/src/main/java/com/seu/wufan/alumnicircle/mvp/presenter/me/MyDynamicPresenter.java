package com.seu.wufan.alumnicircle.mvp.presenter.me;

import android.content.Context;

import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.injector.qualifier.ForApplication;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.presenter.me.IMyDynamicPresenter;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;

import javax.inject.Inject;

/**
 * @author wufan
 * @date 2016/5/17
 */
public class MyDynamicPresenter implements IMyDynamicPresenter{

    private PreferenceUtils preferenceUtils;
    private TokenModel tokenModel;
    private Context appContext;

    @Inject
    public MyDynamicPresenter(@ForApplication Context context, TokenModel tokenModel, PreferenceUtils preferenceUtils) {
        this.tokenModel = tokenModel;
        this.appContext = context;
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    public void init(String user_id) {
        CommunitySDK sdk = CommunityFactory.getCommSDK(appContext);
        sdk.fetchUserTimeLine(user_id, new Listeners.FetchListener<FeedsResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(FeedsResponse feedsResponse) {
//                feedsResponse.resultWithoutTop
            }
        });
    }

    @Override
    public void attachView(IView v) {

    }

    @Override
    public void destroy() {

    }
}
