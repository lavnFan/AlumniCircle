package com.seu.wufan.alumnicircle.ui.fragment.me;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.common.base.BaseFragment;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.EduEditPresenter;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.JobHistoryEditPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEduEditView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IJobEditView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/16
 */
public class ProfExperEditFragment extends BaseFragment implements IJobEditView{

    @Bind(R.id.edit_prof_exper_company_et)
    EditText mCompanyEt;
    @Bind(R.id.edit_prof_exper_job_et)
    EditText mJobEt;
    @Bind(R.id.edit_prof_exper_time_start_tv)
    TextView mTimeStartTv;
    @Bind(R.id.edit_prof_exper_time_end_tv)
    TextView mTimeEndTv;
    @Bind(R.id.edit_prof_exper_job_intro_et)
    EditText mJobIntroEt;

    Job job = new Job();
    TimePickerView pvTimeStart;
    TimePickerView pvTimeEnd;
    EditFragmentToActivityListener listener;

    @Inject
    JobHistoryEditPresenter jobHistoryEditPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_edit_prof_exper_edit;
    }

    @Override
    public void initViews(View view) {
        getApiComponent().inject(this);
        jobHistoryEditPresenter.attachView(this);
    }

    @Override
    public void initDatas() {
        initOptions();
    }

    private void initOptions() {
        //控制时间范围
        Calendar calendar = Calendar.getInstance();

        pvTimeStart = new TimePickerView(getActivity(), TimePickerView.Type.YEAR_MONTH);
        pvTimeStart.setRange(calendar.get(Calendar.YEAR) - 60, calendar.get(Calendar.YEAR));
        pvTimeStart.setTime(new Date());
        pvTimeStart.setCyclic(false);
        pvTimeStart.setCancelable(true);
        //时间选择后回调
        pvTimeStart.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTimeStartTv.setText(getTime(date));
            }
        });

        pvTimeEnd = new TimePickerView(getActivity(), TimePickerView.Type.YEAR_MONTH);
        pvTimeEnd.setRange(calendar.get(Calendar.YEAR) - 60, calendar.get(Calendar.YEAR));
        pvTimeEnd.setTime(new Date());
        pvTimeEnd.setCyclic(false);
        pvTimeEnd.setCancelable(true);
        //时间选择后回调
        pvTimeEnd.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTimeEndTv.setText(getTime(date));
            }
        });
    }

    @OnClick(R.id.edit_prof_exper_time_start_tv)
    void setStartTime(){
        pvTimeStart.show();
    }

    @OnClick(R.id.edit_prof_exper_time_end_tv)
    void setEndTime(){
        pvTimeEnd.show();
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
        return format.format(date);
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick(R.id.edit_prof_exper_delete_edu_tv)
    void deleteJob(){
        //删除求职经历，并回退fragment
        jobHistoryEditPresenter.deleteJob(job.getId());
    }

    public void keepJob(KeepCallBack callBack){
        job.setCompany(mCompanyEt.getText().toString());
        job.setDuration(mTimeStartTv.getText().toString()+"-"+mTimeEndTv.getText().toString());
        job.setJob(mJobEt.getText().toString());
        job.setInfo(mJobIntroEt.getText().toString());
        callBack.keepJob(job);
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
    public void backEdit() {
        listener.backEditFragment();
    }

    public interface KeepCallBack{
            void keepJob(Job job);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EditFragmentToActivityListener) {
            listener = (EditFragmentToActivityListener) activity;
        }
    }
}
