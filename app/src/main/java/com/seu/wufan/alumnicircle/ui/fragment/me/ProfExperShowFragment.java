package com.seu.wufan.alumnicircle.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.api.entity.item.Jobs;
import com.seu.wufan.alumnicircle.common.base.BaseFragment;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.ProfExperShowJobFragmentToActivity;
import com.seu.wufan.alumnicircle.ui.adapter.me.JobItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/16
 */
public class ProfExperShowFragment extends BaseFragment {

    @Bind(R.id.edit_prof_exper_lm_lv)
    LoadMoreListView loadMoreListView;

    ShowJobFragmentToActivityListener listener;
    JobItemAdapter jobItemAdapter;
    private Jobs jobs;
    private int jobPosition = 0;

    public static ProfExperShowFragment newInstance(Jobs jobs) {
        Bundle args = new Bundle();
        args.putSerializable(ProfExperShowJobFragmentToActivity.EXTRA_JOB, jobs);
        ProfExperShowFragment fragment = new ProfExperShowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_edit_prof_exper_show;
    }

    @Override
    public void initViews(View view) {
        jobs = (Jobs) getArguments().getSerializable(ProfExperShowJobFragmentToActivity.EXTRA_JOB);
    }

    @Override
    public void initDatas() {
        jobItemAdapter = new JobItemAdapter(getActivity());
        jobItemAdapter.setmEntities(jobs.getJobs());
        loadMoreListView.setAdapter(jobItemAdapter);
        jobItemAdapter.notifyDataSetChanged();
        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //根据id进入对应的职业，进行修改
                jobPosition = position;
                listener.replaceFragment(jobs.getJobs().get(position));
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick(R.id.me_edit_info_prof_exper_show_linear_layout)
    public void onClick() {
        listener.replaceFragment(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ShowJobFragmentToActivityListener) {
            listener = (ShowJobFragmentToActivityListener) activity;
        }
    }

    public Jobs updateShow(Job job, int REQUEST_CODE) {    //当数据发生改变时，fragment进行更新
        switch (REQUEST_CODE) {
            case ProfExperShowJobFragmentToActivity.REQUEST_ADD:
                List<Job> jobs = new ArrayList<>();
                jobs.add(job);
                jobItemAdapter.addEntities(jobs);
                jobItemAdapter.notifyDataSetChanged();
                break;
            case ProfExperShowJobFragmentToActivity.REQUEST_UPDATE:
                this.jobs.getJobs().get(jobPosition).setJob(job.getJob());
                this.jobs.getJobs().get(jobPosition).setDuration(job.getDuration());
                jobItemAdapter.setmEntities(this.jobs.getJobs());
                jobItemAdapter.notifyDataSetChanged();
                break;
            case ProfExperShowJobFragmentToActivity.REQUEST_DELETE:
                this.jobs.getJobs().remove(jobPosition);
                jobItemAdapter.setmEntities(this.jobs.getJobs());
                jobItemAdapter.notifyDataSetChanged();
                break;
        }
        return this.jobs;
    }

}
