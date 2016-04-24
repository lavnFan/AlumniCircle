package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.RegisterReq;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.mvp.views.activity.IRegisterView;

import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class RegisterActivity extends BaseActivity implements IRegisterView{

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

    @Override
    protected void prepareData() {

    }

    @LayoutRes
    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViewsAndEvents() {

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


}