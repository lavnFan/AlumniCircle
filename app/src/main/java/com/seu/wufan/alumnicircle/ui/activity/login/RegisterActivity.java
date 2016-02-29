package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.LoginRes;
import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.common.utils.NetUtils;
import com.seu.wufan.alumnicircle.common.utils.PreferenceUtils;
import com.seu.wufan.alumnicircle.common.utils.TLog;

import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

    RegisterReq req = new RegisterReq();

    @LayoutRes
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
                selectEnrollYear();
                break;
            case R.id.register_college_ll:
                      //选择院系

                break;
            case R.id.register_prof_ll:
                    //选择专业

                break;
        }
    }

    private void selectEnrollYear() {
        Calendar calendar = Calendar.getInstance();
        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(calendar.get(Calendar.YEAR) - 40)
                .maxValue(calendar.get(Calendar.YEAR) + 5)
                .defaultValue(calendar.get(Calendar.YEAR))
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.BLUE)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();
        new AlertDialog.Builder(this)
                .setTitle("请选择入学年份")
                .setView(numberPicker)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEnrollYearEt.setText(String.valueOf(numberPicker.getValue()));
                    }
                })
                .show();
    }

    @OnClick(R.id.register_btn)
    void register() {
        registerCase();
    }

    private void registerCase() {
        validateRegister();
        if (NetUtils.isNetworkConnected(this)) {
            ApiManager.getService(this).register(req, new Callback<LoginRes>() {
                @Override
                public void success(LoginRes loginRes, Response response) {
                    TLog.i("Register",loginRes.getAccess_token()+" "+loginRes.getUser_id());
                    saveUserInfo(loginRes);
                    readyGoThenKill(MainActivity.class);
                }

                @Override
                public void failure(RetrofitError error) {
                    showInnerError(error);
                }
            });

        }else {
            showNetWorkError();
        }
    }

    private void validateRegister() {
        req.setPhone_num(mPhonenNumEt.getText().toString());
        req.setPassword(mPasswordEt.getText().toString());
        req.setEnroll_year(Integer.parseInt(mEnrollYearEt.getText().toString()));
        req.setSchool(mCollegeEt.getText().toString());
        req.setMajor(mProfEt.getText().toString());
    }

    private void saveUserInfo(LoginRes res) {
        PreferenceUtils.putString(RegisterActivity.this.getApplicationContext(),
                PreferenceUtils.Key.ACCESS, res.getAccess_token());
        PreferenceUtils.putString(RegisterActivity.this.getApplicationContext(),
                PreferenceUtils.Key.USER_ID, res.getUser_id());
    }

}