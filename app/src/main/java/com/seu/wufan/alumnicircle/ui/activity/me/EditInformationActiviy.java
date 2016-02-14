package com.seu.wufan.alumnicircle.ui.activity.me;

import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.utils.TLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class EditInformationActiviy extends BaseSwipeActivity {
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.me_edit_info_photo_tr)
    TableRow mPhotoTr;
    @Bind(R.id.me_edit_info_name_tr)
    TableRow mNameTr;
    @Bind(R.id.me_edit_info_sex_tr)
    TableRow mSexTr;
    @Bind(R.id.me_edit_info_birth_date_tr)
    TableRow mBirthDateTr;
    @Bind(R.id.me_edit_info_work_city_tr)
    TableRow mWorkCityTr;
    @Bind(R.id.me_edit_info_job_prof_tr)
    TableRow mJobProfTr;
    @Bind(R.id.me_edit_info_company_tr)
    TableRow mCompanyTr;
    @Bind(R.id.me_edit_info_prof_tr)
    TableRow mProfTr;
    @Bind(R.id.me_edit_info_person_intro_tr)
    TableRow mPersonIntroTr;
    @Bind(R.id.me_edit_info_prof_exper_tr)
    TableRow mProfExperTr;
    @Bind(R.id.me_edit_info_educ_exper_tr)
    TableRow mEducExperTr;

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_information;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick({R.id.me_edit_info_photo_tr, R.id.me_edit_info_name_tr, R.id.me_edit_info_sex_tr,
            R.id.me_edit_info_birth_date_tr, R.id.me_edit_info_work_city_tr, R.id.me_edit_info_job_prof_tr,
            R.id.me_edit_info_company_tr, R.id.me_edit_info_prof_tr, R.id.me_edit_info_person_intro_tr,
            R.id.me_edit_info_prof_exper_tr, R.id.me_edit_info_educ_exper_tr})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.me_edit_info_photo_tr:
                break;
            case R.id.me_edit_info_name_tr:
                break;
            case R.id.me_edit_info_sex_tr:
                final String [] sexs = new String[]{"男", "女", "保密"};
                new AlertView(null, null, "取消", null,sexs, this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                            if(position!=AlertView.CANCELPOSITION){
                                TLog.i("sex selected:", sexs[position]);
                            }
                    }
                }).setCancelable(true).show();
                break;
            case R.id.me_edit_info_birth_date_tr:
                break;
            case R.id.me_edit_info_work_city_tr:
                break;
            case R.id.me_edit_info_job_prof_tr:
                break;
            case R.id.me_edit_info_company_tr:
                break;
            case R.id.me_edit_info_prof_tr:
                break;
            case R.id.me_edit_info_person_intro_tr:
                break;
            case R.id.me_edit_info_prof_exper_tr:
                break;
            case R.id.me_edit_info_educ_exper_tr:
                break;
        }
    }
}
