package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.TimePickerView;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.utils.TLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class RegisterActivity extends BaseActivity {

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.register_phonen_num_et)
    EditText mPhonenNumEt;
    @Bind(R.id.register_password_et)
    EditText mPasswordEt;
    @Bind(R.id.register_enroll_year_tv)
    TextView mEnrollYearEt;
    @Bind(R.id.register_college_tv)
    TextView mCollegeEt;
    @Bind(R.id.register_major_tv)
    TextView mProfEt;

    public final static int REQUESTCODE_College = 0;
    public final static int REQUESTCODE_Prof = 1;

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

    @OnClick({R.id.register_enroll_year_ll, R.id.register_college_ll, R.id.register_prof_ll})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.register_enroll_year_ll:
                break;
            case R.id.register_college_ll:
                bundle.putString(RegisterCollegeActivity.EXTRA_College, mCollegeEt.getText().toString());
                readyGoForResult(RegisterCollegeActivity.class, REQUESTCODE_College, bundle);
                break;
            case R.id.register_prof_ll:
                bundle.putString(RegisterProfActivity.EXTRA_Prof, mProfEt.getText().toString());
                readyGoForResult(RegisterProfActivity.class, REQUESTCODE_Prof, bundle);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUESTCODE_College:
                String college = (data == null) ? String.valueOf(R.string.college_hint) : data.getStringExtra(RegisterCollegeActivity.EXTRA_College);
                mCollegeEt.setText(college);
                break;
            case REQUESTCODE_Prof:
                String prof = (data == null) ? String.valueOf(R.string.major_hint) : data.getStringExtra(RegisterProfActivity.EXTRA_Prof);
                mProfEt.setText(prof);
                break;
        }
    }


}
