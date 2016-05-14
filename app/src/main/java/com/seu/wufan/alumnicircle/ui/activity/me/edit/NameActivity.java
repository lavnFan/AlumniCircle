package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.mvp.presenter.me.NamePresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.INameView;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.tencent.connect.UserInfo;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/20
 */
public class NameActivity extends BaseSwipeActivity implements INameView{
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;
    @Bind(R.id.edit_name_et)
    EditText mNameEt;

    @Inject
    NamePresenter namePresenter;

    public final static String EXTRA_NAME="name";

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_name;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        namePresenter.attachView(this);
    }

    @Override
    protected void initViewsAndEvents() {
        initToolBars();
        final UserInfoRes userInfoRes= (getIntent().getExtras()==null)?null: (UserInfoRes) getIntent().getExtras().getSerializable(EXTRA_NAME);
        TLog.i("Name:",userInfoRes.getName());
        mNameEt.setText(userInfoRes.getName());
        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_NAME, mNameEt.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(EditInformationActivity.REQUESTCODE_Name, intent);

                userInfoRes.setName(mNameEt.getText().toString());
                namePresenter.updateName(userInfoRes);
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    private void initToolBars() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(R.string.edit_information);
        mToolbarRightTv.setVisibility(View.VISIBLE);
        mToolbarRightTv.setText(R.string.keep);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    protected void onDestroy() {
        super.onDestroy();
        namePresenter.destroy();
    }

    @Override
    public void destroy() {
        finish();
    }
}
