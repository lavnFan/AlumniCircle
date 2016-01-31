package com.seu.wufan.alumnicircle.ui.activity.login;

import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class RegisterActivity extends BaseActivity {

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;

    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {

        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(R.string.register);
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
