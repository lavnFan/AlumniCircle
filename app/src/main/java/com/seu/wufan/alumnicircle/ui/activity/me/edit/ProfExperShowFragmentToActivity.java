package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.api.entity.item.Jobs;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.EduShowPresenter;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.JobHistoryEditPresenter;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.JobHistoryShowPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEduShowView;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IJobShowView;
import com.seu.wufan.alumnicircle.ui.fragment.me.EditFragmentToActivityListener;
import com.seu.wufan.alumnicircle.ui.fragment.me.ShowFragmentToActivityListener;
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
public class ProfExperShowFragmentToActivity extends BaseSwipeActivity implements ShowFragmentToActivityListener,EditFragmentToActivityListener,IJobShowView {
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;

    ProfExperShowFragment showFragment;
    ProfExperEditFragment editFragment;

    private List<Job> jobList = new ArrayList<>();
    private Jobs jobs = new Jobs();
    public static final String EXTRA_JOB="job";

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
        if (showFragment == null) {
            showFragment = ProfExperShowFragment.newInstance(jobs);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_prof_exper_container, showFragment).commit();

        initToolBars();

        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFragment.keepJob(new ProfExperEditFragment.KeepCallBack() {
                    @Override
                    public void keepJob(Job job) {     //回调editFragment中的保存事件，再回退fragment
                        jobHistoryShowPresenter.saveJob(job);
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
    public void replaceFragment(String id) {
        if (editFragment == null) {
            editFragment = new ProfExperEditFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_prof_exper_container, editFragment).addToBackStack(null).commit();
        mToolbarRightTv.setVisibility(View.VISIBLE);
        mToolbarRightTv.setText(R.string.keep);
    }

    public void backFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        mToolbarRightTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void backEditFragment() {
        backFragment();
    }

    @Override
    public void onBackPressed() {
        if(editFragment!=null){
            mToolbarRightTv.setVisibility(View.INVISIBLE);
        }
        super.onBackPressed();
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
        backFragment();
    }
}
