package com.seu.wufan.alumnicircle.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Edus;
import com.seu.wufan.alumnicircle.common.base.BaseFragment;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.activity.me.edit.EducationActivity;
import com.seu.wufan.alumnicircle.ui.adapter.me.EduItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/5/16
 */
public class EducationShowFragment extends BaseFragment {

    @Bind(R.id.edit_edu_lm_lv)
    LoadMoreListView loadMoreListView;

    ShowEduFragmentToActivityListener listener;
    EduItemAdapter eduItemAdapter;
    private Edus edus = new Edus();
    private int eduPosition = 0;

    public static EducationShowFragment newInstance(Edus edus) {
        Bundle args = new Bundle();
        args.putSerializable(EducationActivity.EXTRA_EDU, edus);
        EducationShowFragment fragment = new EducationShowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_education_show;
    }

    @Override
    public void initViews(View view) {
        edus = (Edus) getArguments().getSerializable(EducationActivity.EXTRA_EDU);
    }

    @Override
    public void initDatas() {
        eduItemAdapter = new EduItemAdapter(getActivity());
        eduItemAdapter.setmEntities(edus.getEdus());
        loadMoreListView.setAdapter(eduItemAdapter);
        eduItemAdapter.notifyDataSetChanged();
        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //根据id进入对应的职业，进行修改
                eduPosition = position;
                listener.replaceFragment(edus.getEdus().get(position));
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick(R.id.me_edit_info_edu_show_linear_layout)
    public void onClick() {
        listener.replaceFragment(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ShowEduFragmentToActivityListener) {
            listener = (ShowEduFragmentToActivityListener) activity;
        }
    }

    public Edus updateShow(Edu edu, int REQUEST_CODE) {    //当数据发生改变时，fragment进行更新
        switch (REQUEST_CODE) {
            case EducationActivity.REQUEST_ADD:
                List<Edu> edus = new ArrayList<>();
                edus.add(edu);
                eduItemAdapter.addEntities(edus);
                eduItemAdapter.notifyDataSetChanged();
                break;
            case EducationActivity.REQUEST_UPDATE:
                this.edus.getEdus().get(eduPosition).setSchool(edu.getSchool());
                this.edus.getEdus().get(eduPosition).setDuration(edu.getDuration());
                eduItemAdapter.setmEntities(this.edus.getEdus());
                eduItemAdapter.notifyDataSetChanged();
                break;
            case EducationActivity.REQUEST_DELETE:
                this.edus.getEdus().remove(eduPosition);
                eduItemAdapter.setmEntities(this.edus.getEdus());
                eduItemAdapter.notifyDataSetChanged();
                break;
        }
        return this.edus;
    }
}
