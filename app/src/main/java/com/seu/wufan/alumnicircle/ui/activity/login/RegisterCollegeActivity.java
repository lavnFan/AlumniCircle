package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActiviy;
import com.seu.wufan.alumnicircle.ui.utils.TLog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author wufan
 * @date 2016/2/21
 */
public class RegisterCollegeActivity extends BaseActivity {
    public final static String EXTRA_College = "college";
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;
    @Bind(R.id.register_college_et)
    EditText mCollegeEt;

    @Override
    protected int getContentView() {
        return R.layout.activity_register_college;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        initToolBars();
        String college = (getIntent().getExtras() == null) ? null : getIntent().getExtras().getString(EXTRA_College);
        TLog.i("college:", college);
        mCollegeEt.setText(college);
        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_College, mCollegeEt.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RegisterActivity.REQUESTCODE_College, intent);
                finish();
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
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_College, mCollegeEt.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RegisterActivity.REQUESTCODE_College, intent);
        super.onBackPressed();
    }
}
