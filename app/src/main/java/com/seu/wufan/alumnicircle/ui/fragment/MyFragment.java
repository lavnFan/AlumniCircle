package com.seu.wufan.alumnicircle.ui.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.mvp.presenter.me.MyPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IMyView;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActiviy;
import com.seu.wufan.alumnicircle.ui.activity.me.MyCollectionActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyDynamicActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyMessageActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyQrcodeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.SettingSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class MyFragment extends BaseLazyFragment implements IMyView{

    CommUser user = new CommUser();

    @Inject
    MyPresenter myPresenter;

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void prepareData() {
        getApiComponent().inject(this);
        myPresenter.attachView(this);
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_me;
    }

    @OnClick(R.id.me_my_information_relative_layout)
    void myInformation() {
        Intent intent = new Intent(getActivity(),
                MyInformationActivity.class);
        myPresenter.getUid();
        intent.putExtra(Constants.TAG_USER, user);
        startActivity(intent);
    }

    @OnClick(R.id.me_edit_information_relative_layout)
    void editInformation() {
        readyGo(EditInformationActiviy.class);
    }

    @OnClick(R.id.me_my_qrcode_relative_layout)
    void myQrcode() {
        readyGo(MyQrcodeActivity.class);
    }

    @OnClick(R.id.me_my_dynamic_relative_layout)
    void myDynamic() {
        readyGo(MyDynamicActivity.class);
    }

    @OnClick(R.id.me_my_message_relative_layout)
    void myMessage() {
        readyGo(MyMessageActivity.class);
    }

    @OnClick(R.id.me_my_collection_relative_layout)
    void myCollection() {
        readyGo(MyCollectionActivity.class);
    }

    @OnClick(R.id.me_setting_relative_layout)
    void setting() {
        readyGo(SettingSwipeActivity.class);
    }

    @Override
    public void setUser(CommUser user) {
        this.user = user;
    }

    @Override
    public void showNetCantUse() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showToast(@NonNull String s) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myPresenter.destroy();
    }
}
