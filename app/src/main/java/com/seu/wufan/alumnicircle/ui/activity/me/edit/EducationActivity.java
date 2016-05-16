package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.fragment.me.ProfExperEditFragment;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/5/14
 */
public class EducationActivity extends BaseSwipeActivity{

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;
    public static final String EXTRA_EDU="edu";

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_education;
    }

    @Override
    protected void initViewsAndEvents() {
        initToolBars();

        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                editFragment.keepJob(new ProfExperEditFragment.KeepCallBack() {
//                    @Override
//                    public void keepJob(Job job) {     //回调editFragment中的保存事件，再回退fragment
//                        if (job.getId() != null) {
//                            jobHistoryShowPresenter.updateJob(job.getId(), job);
//                        } else {
//                            jobHistoryShowPresenter.saveJob(job);
//                        }
//                    }
//                });
            }
        });
    }
    private void initToolBars() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(R.string.edu_exp);
    }
    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
