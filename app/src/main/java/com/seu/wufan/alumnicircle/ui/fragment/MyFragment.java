package com.seu.wufan.alumnicircle.ui.fragment;

import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActiviy;
import com.seu.wufan.alumnicircle.ui.activity.me.MyCollectionActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyDynamicActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyMessageActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyQrcodeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.SettingActivity;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;

import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class MyFragment extends BaseLazyFragment {

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
    protected void initViewsAndEvents() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_me;
    }

    @OnClick(R.id.me_my_information_relative_layout)
    void myInformation() {
        readyGo(MyInformationActivity.class);
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
        readyGo(SettingActivity.class);
    }
}
