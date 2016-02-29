package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActiviy;

import butterknife.Bind;


/**
 * @author wufan
 * @date 2016/2/20
 */
public class JobActivity extends BaseSwipeActivity {
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;
    @Bind(R.id.edit_job_et)
    EditText mJobEt;
    public final static String EXTRA_JOB="job";

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_job;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        initToolBars();

        String job= (getIntent().getExtras()==null)?null:getIntent().getExtras().getString(EXTRA_JOB);
        mJobEt.setText(job);
        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_JOB, mJobEt.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(EditInformationActiviy.REQUESTCODE_Job, intent);
                finish();
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    private void initToolBars() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(R.string.prof);
        mToolbarRightTv.setVisibility(View.VISIBLE);
        mToolbarRightTv.setText(R.string.keep);
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_JOB, mJobEt.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(EditInformationActiviy.REQUESTCODE_Job, intent);
        super.onBackPressed();
    }
}
