package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.api.entity.item.Jobs;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.JobHistoryShowPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IJobShowView;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActivity;
import com.seu.wufan.alumnicircle.ui.fragment.me.EditFragmentToActivityListener;
import com.seu.wufan.alumnicircle.ui.fragment.me.ShowJobFragmentToActivityListener;
import com.seu.wufan.alumnicircle.ui.fragment.me.ProfExperEditFragment;
import com.seu.wufan.alumnicircle.ui.fragment.me.ProfExperShowFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/16
 */
public class ProfExperShowJobFragmentToActivity extends BaseSwipeActivity implements ShowJobFragmentToActivityListener, EditFragmentToActivityListener, IJobShowView {
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;

    ProfExperShowFragment showFragment;
    ProfExperEditFragment editFragment;

    private List<Job> jobList = new ArrayList<>();
    private Jobs jobs = new Jobs();
    public static final String EXTRA_JOB = "job";
    public static final String EXTRA_JOB_ID = "job_id";
    public static final String EXTRA_JOB_LIST = "job_list";
    public static final int REQUEST_UPDATE = 1;
    public static final int REQUEST_ADD = 2;
    public static final int REQUEST_DELETE = 3;

    @Inject
    JobHistoryShowPresenter jobHistoryShowPresenter;

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_prof_exper;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        jobHistoryShowPresenter.attachView(this);
        UserInfoDetailRes userInfoDetailRes = (UserInfoDetailRes) getIntent().getExtras().getSerializable(EXTRA_JOB);
        jobList = userInfoDetailRes.getJobHistory();
        jobs.setJobs(jobList);
    }

    @Override
    protected void initViewsAndEvents() {
        showFragment = ProfExperShowFragment.newInstance(jobs);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_prof_exper_container, showFragment).commit();

        initToolBars();

        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFragment.keepJob(new ProfExperEditFragment.KeepCallBack() {
                    @Override
                    public void keepJob(Job job) {     //回调editFragment中的保存事件，再回退fragment
                        if (job.getId() != null) {
                            jobHistoryShowPresenter.updateJob(job.getId(), job);
                        } else {
                            jobHistoryShowPresenter.saveJob(job);
                        }
                    }
                });
            }
        });
    }

    private void initToolBars() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(R.string.prof_exp);
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void replaceFragment(Job job) {
        editFragment = ProfExperEditFragment.newInstance(job);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_prof_exper_container, editFragment).addToBackStack(null).commit();
        mToolbarRightTv.setVisibility(View.VISIBLE);
        mToolbarRightTv.setText(R.string.keep);
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
        ToastUtils.showToast(s, this);
    }

    @Override
    public void backEdit(Job job, int REQUEST_CODE) {   //presenter通知视图层更新,根据是否为添加还是修改
        //1、先回退EditFragment
        backFragment();
        //2、再通知showFragment进行视图更新
        jobs = showFragment.updateShow(job, REQUEST_CODE);
    }

    @Override
    public void backEditFragment(int REQUEST_CODE) {      //EditFragment调用的接口，点击删除时进行回退
        jobs = showFragment.updateShow(null, REQUEST_CODE);
        backFragment();
    }

    public void backFragment() {          //回退EditFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        mToolbarRightTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {     //当没有点击保存时，可直接回退
        if (editFragment != null) {
            mToolbarRightTv.setVisibility(View.INVISIBLE);
        }
        //否则回退更新信息
        TLog.i("TAG", "回退到edit information activity！");
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_JOB_LIST, jobs);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(EditInformationActivity.REQUESTCODE_JOB_HISTORY, intent);

        super.onBackPressed();
    }

}
