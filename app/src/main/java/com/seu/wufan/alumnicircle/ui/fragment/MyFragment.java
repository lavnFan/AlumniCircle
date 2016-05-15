package com.seu.wufan.alumnicircle.ui.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.MyPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IMyView;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyCollectionActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyDynamicActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyMessageActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyQrcodeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.SettingSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.NameActivity;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class MyFragment extends BaseLazyFragment implements IMyView {

    CommUser user = new CommUser();

    @Bind(R.id.my_name_tv)
    TextView mNameTv;
    @Bind(R.id.my_photo_cv)
    CircleImageView mPhotoCv;

    @Inject
    MyPresenter myPresenter;

    public final static int REQUESTCODE_PHOTO = 1;
    public final static int REQUESTCODE_Name = 2;
    public final static int REQUESTCODE = 3;

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
        myPresenter.init();

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
        readyGoForResult(EditInformationActivity.class, REQUESTCODE);
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
    public void setName(String name) {
        mNameTv.setText(name);
    }

    @Override
    public void setPhoto(String photo_path) {
        if (photo_path != null) {
            CommonUtils.showCircleImageWithGlide(getActivity(), mPhotoCv, photo_path);
        }
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(getActivity());
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(getActivity());
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myPresenter.destroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE) {
            switch (resultCode) {
                case REQUESTCODE_Name:
                    String name = (data == null) ? null : data.getStringExtra(NameActivity.EXTRA_NAME);
                    if (name != null) {
                        mNameTv.setText(name);
                    }
                    break;
                case REQUESTCODE_PHOTO:
                    String photo_path = (data == null) ? null : data.getStringExtra(EditInformationActivity.EXTRA_PHOTO_PATH);
                    if(photo_path!=null){
                        CommonUtils.showCircleImageWithGlide(getActivity(),mPhotoCv,photo_path);
                    }
                    break;
            }
        }
    }
}
