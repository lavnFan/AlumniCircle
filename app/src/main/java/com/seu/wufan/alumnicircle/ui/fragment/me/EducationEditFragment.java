package com.seu.wufan.alumnicircle.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.common.base.BaseFragment;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.EduEditPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEduEditView;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.EducationActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/5/16
 */
public class EducationEditFragment extends BaseFragment implements IEduEditView {

    TimePickerView pvTimeStart;
    TimePickerView pvTimeEnd;
    EditFragmentToActivityListener listener;
    @Bind(R.id.edit_edu_school_et)
    EditText mSchoolEt;
    @Bind(R.id.edit_edu_major_et)
    EditText mMajorEt;
    @Bind(R.id.edit_edu_degree_et)
    EditText mDegreeEt;
    @Bind(R.id.edit_edu_start_tv)
    TextView mStartTv;
    @Bind(R.id.edit_edu_end_tv)
    TextView mEndTv;
    @Bind(R.id.edit_edu_intro_et)
    EditText mIntroEt;
    @Bind(R.id.edit_edu_delete_edu_tv)
    TextView mDeleteTv;

    private Edu edu = new Edu();

    @Inject
    EduEditPresenter eduEditPresenter;

    public static EducationEditFragment newInstance(Edu edu) {
        Bundle args = new Bundle();
        args.putSerializable(EducationActivity.EXTRA_EDU_ID, edu);
        EducationEditFragment fragment = new EducationEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_education_edit;
    }

    @Override
    public void initViews(View view) {
        getApiComponent().inject(this);
        eduEditPresenter.attachView(this);
    }

    @Override
    public void initDatas() {
        initOptions();
        edu = (Edu) getArguments().getSerializable(EducationActivity.EXTRA_EDU_ID);
        if (edu != null) {
            mSchoolEt.setText(edu.getSchool());
            mMajorEt.setText(edu.getMajor());
            mDegreeEt.setText(edu.getDegree());
            mIntroEt.setText(edu.getInfo());

            //时间需要分裂显示
            String[] timeArray = edu.getDuration().split("-");
            mStartTv.setText(timeArray[0]);
            mEndTv.setText(timeArray[1]);
        } else {
            mDeleteTv.setVisibility(View.INVISIBLE);
        }
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
                mStartTv.setText(getTime(date));
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
                mEndTv.setText(getTime(date));
            }
        });
    }

    @OnClick(R.id.edit_edu_start_tv)
    void setStartTime() {
        pvTimeStart.show();
    }

    @OnClick(R.id.edit_edu_end_tv)
    void setEndTime() {
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

    @OnClick(R.id.edit_edu_delete_edu_tv)
    void deleteEdu() {
        //删除求职经历，并回退fragment
        eduEditPresenter.deleteEdu(edu.getId());
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(getActivity());
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(getActivity());
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, getActivity());
    }

    @Override
    public void backEdit(int REQUEST_CODE) {         //通知activity回退该fragment
        listener.backEditFragment(REQUEST_CODE);
    }

    @OnClick(R.id.edit_edu_delete_edu_tv)
    public void onClick() {
    }

    public interface KeepCallBack {
        void keepEdu(Edu edu);
    }

    public void keepEdu(KeepCallBack callBack) {    //让activity回调，消息为fragment传递到activity
        Edu edu = new Edu();
        if (this.edu != null) {
            edu = this.edu;
        }
        if (mSchoolEt.getText().toString().isEmpty()
                || mStartTv.getText().toString().isEmpty()
                || mEndTv.getText().toString().isEmpty()
                || mMajorEt.getText().toString().isEmpty()
                || mIntroEt.getText().toString().isEmpty()
                ||mDegreeEt.getText().toString().isEmpty()) {
            showToast("请填写必填项！");
        } else {
            edu.setSchool(mSchoolEt.getText().toString());
            edu.setDuration(mStartTv.getText().toString() + "-" + mEndTv.getText().toString());
            edu.setMajor(mMajorEt.getText().toString());
            edu.setInfo(mIntroEt.getText().toString());
            edu.setDegree(mDegreeEt.getText().toString());
            callBack.keepEdu(edu);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EditFragmentToActivityListener) {
            listener = (EditFragmentToActivityListener) activity;
        }
    }
}
