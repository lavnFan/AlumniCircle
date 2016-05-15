package com.seu.wufan.alumnicircle.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.api.entity.item.Jobs;
import com.seu.wufan.alumnicircle.common.base.BaseFragment;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.ProfExperShowFragmentToActivity;
import com.seu.wufan.alumnicircle.ui.adapter.me.JobItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;

import java.io.Serializable;
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

    ShowFragmentToActivityListener listener;
    private Jobs jobs;
    JobItemAdapter jobItemAdapter;

    public static ProfExperShowFragment newInstance(Jobs jobs){
        Bundle args = new Bundle();
        args.putSerializable(ProfExperShowFragmentToActivity.EXTRA_JOB,jobs);
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
        jobs = (Jobs) getArguments().getSerializable(ProfExperShowFragmentToActivity.EXTRA_JOB);
    }

    @Override
    public void initDatas() {
        jobItemAdapter  = new JobItemAdapter(getActivity());
        jobItemAdapter.setmEntities(jobs.getJobs());
        loadMoreListView.setAdapter(jobItemAdapter);
        jobItemAdapter.notifyDataSetChanged();
        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //根据id进入对应的职业，进行修改
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
        if(activity instanceof ShowFragmentToActivityListener){
            listener = (ShowFragmentToActivityListener) activity;
        }
    }
}
