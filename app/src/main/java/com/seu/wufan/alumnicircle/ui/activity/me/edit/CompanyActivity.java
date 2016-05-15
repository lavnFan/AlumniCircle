package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.CompanyPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.ICompanyView;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActivity;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/20
 */
public class CompanyActivity extends BaseSwipeActivity implements ICompanyView{

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;
    @Bind(R.id.edit_company_et)
    EditText mCompanyEt;

    public final static String EXTRA_COMPANY="company";
    private UserInfoDetailRes userInfoDetailRes = new UserInfoDetailRes();

    @Inject
    CompanyPresenter companyPresenter;

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_company;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        companyPresenter.attachView(this);
    }

    @Override
    protected void initViewsAndEvents() {
        initToolBars();

        userInfoDetailRes= (getIntent().getExtras()==null)?null: (UserInfoDetailRes) getIntent().getExtras().getSerializable(EXTRA_COMPANY);
        mCompanyEt.setText(userInfoDetailRes.getCompany());

        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_COMPANY, mCompanyEt.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(EditInformationActivity.REQUESTCODE_Company, intent);
                userInfoDetailRes.setCompany(mCompanyEt.getText().toString());
                companyPresenter.updateCompany(userInfoDetailRes);
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
        mToolbarTv.setText(R.string.company);
        mToolbarRightTv.setVisibility(View.VISIBLE);
        mToolbarRightTv.setText(R.string.keep);
    }

    @Override
    public void destroy() {
        finish();
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(this);
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(this);
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s,this);
    }
}
