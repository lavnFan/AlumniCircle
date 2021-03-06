package com.seu.wufan.alumnicircle.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.common.base.BaseActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.widget.dateprovider.DataProvider;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.login.RegisterIPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IRegisterView;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.dialog.ProgressDialog;
import com.seu.wufan.alumnicircle.ui.widget.MajorPickerView;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class RegisterActivity extends BaseActivity implements IRegisterView {

    @Bind(R.id.telephone_number_edittext)
    EditText telephoneNumberEditText;
    @Bind(R.id.password_edittext)
    EditText passwordEditText;
    @Bind(R.id.enroll_year_textview)
    TextView enrollYearTextView;
    @Bind(R.id.department_textview)
    TextView departmentTextView;
    @Bind(R.id.major_textview)
    TextView majorTextView;
    @Bind(R.id.agree_with_protocol)
    CheckBox agreeWithProtocolCheckBox;
    @Bind(R.id.register_name_et)
    EditText mNameTv;
    @Bind(R.id.register_number_et)
    EditText mNumberEt;
    @Bind(R.id.register_top_phone_ll)
    LinearLayout mTopLl;

    @Inject
    RegisterIPresenter registerPresenter;

    public static final String EXTRA_WEIXIN = "weixin";
    private boolean isWeixin = false;
    private UserInfoRes userInfoRes = new UserInfoRes();
    public static final String EXTRA_USER_ID = "user_id";
    private String user_id = null;

    private UMShareAPI mShareAPI;

    private OptionsPickerView enrollYearPickerView;
    private MajorPickerView majorPickerView;

    private DataProvider.SeuMajors seuMajors;
    private ArrayList<String> enrollYears;



    private boolean enrollYearPickerViewIsShowing = false;
    private boolean majorPickerViewIsShowing = false;

    ProgressDialog pd;

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        registerPresenter.attachView(this);

        String weixin = getIntent().getStringExtra(EXTRA_WEIXIN);
        isWeixin = (weixin == null) ? false : true;
        user_id = getIntent().getStringExtra(EXTRA_USER_ID);
        TLog.i("TAG",isWeixin+user_id);
        initUser();
    }

    private void initUser() {
        if (isWeixin) {
            mShareAPI = UMShareAPI.get(this);
            SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
            mShareAPI.getPlatformInfo(this, platform, umAuthListener);
        }
    }

    @LayoutRes
    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViewsAndEvents() {
        seuMajors = DataProvider.getSeuMajorsData();
        enrollYears = DataProvider.getEnrollYearsData();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    /**
     * 设置入学年份
     */
    @OnClick(R.id.enroll_year_linearlayout)
    public void enrollYearLayoutOnClick() {
        enrollYearPickerView = new OptionsPickerView(this);
        enrollYearPickerView.setPicker(enrollYears);
        enrollYearPickerView.setTitle("选择入学年份");
        enrollYearPickerView.setCyclic(false);
        enrollYearPickerView.setSelectOptions(enrollYears.size() - 1);
        enrollYearPickerView.setCancelable(true);
        enrollYearPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                String enrollYear = enrollYears.get(option1);
                enrollYearTextView.setText(enrollYear);
                enrollYearPickerViewIsShowing = false;
            }
        });
        enrollYearPickerView.show();
        enrollYearPickerViewIsShowing = true;
    }

    /**
     * 设置学院、专业
     */
    @OnClick({R.id.major_linearlayout, R.id.department_linearlayout})
    public void setDepartmentAndMajor() {
        majorPickerView = new MajorPickerView(this);
        majorPickerView.setPicker(seuMajors.getDepartments(), seuMajors.getMajors(), true);
        majorPickerView.setTitle("选择院系和专业");
        majorPickerView.setCyclic(true, false, false);
        majorPickerView.setSelectOptions(0, 0);
        majorPickerView.setCancelable(true);

        majorPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                String department = seuMajors.getDepartments().get(option1);
                String major = seuMajors.getMajors().get(option1).get(option2);
                departmentTextView.setText(department);
                majorTextView.setText(major);
                majorPickerViewIsShowing = false;
            }
        });
        majorPickerView.show();
        majorPickerViewIsShowing = true;
    }

    @OnClick(R.id.register_button)
    void register() {
        if (agreeWithProtocolCheckBox.isChecked()) {
            if (isWeixin) {
                if(registerPresenter.isValid("11111111111", "288N212",
                        enrollYearTextView.getText().toString(), departmentTextView.getText().toString(),
                        majorTextView.getText().toString(), mNameTv.getText().toString(), mNumberEt.getText().toString())){

                    userInfoRes.setUser_id(user_id);
                    userInfoRes.setName(mNameTv.getText().toString());
                    userInfoRes.setEnroll_year(enrollYearTextView.getText().toString());
                    userInfoRes.setSchool(departmentTextView.getText().toString());
                    userInfoRes.setMajor(majorTextView.getText().toString());
                    userInfoRes.setStudent_num(mNumberEt.getText().toString());
                    userInfoRes.setImage("");

                    registerPresenter.doWeixin(userInfoRes);
                }
            } else {
                registerPresenter.doRegister(telephoneNumberEditText.getText().toString(), passwordEditText.getText().toString(),
                        enrollYearTextView.getText().toString(), departmentTextView.getText().toString(), majorTextView.getText().toString(), mNameTv.getText().toString(), mNumberEt.getText().toString());
            }
        } else {
            ToastUtils.showToast("请阅读校友圈注册协议", this);
        }
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
    public void registerSuccess() {
        if (pd != null) {
            pd.dismiss();
        }
        readyGoThenKill(MainActivity.class);
    }

    @Override
    public void registerLoading() {
        pd = new ProgressDialog(this);
        pd.setContent("正在注册");
        pd.show();
    }

    @Override
    public void registerFailed() {
        if (pd != null) {
            pd.dismiss();
        }
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerPresenter.destroy();
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (i == UMAuthListener.ACTION_GET_PROFILE && map != null) {
                mTopLl.setVisibility(View.GONE);
                mNameTv.setText(map.get("nickname"));
                userInfoRes.setImage(map.get("headimgurl"));
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

}