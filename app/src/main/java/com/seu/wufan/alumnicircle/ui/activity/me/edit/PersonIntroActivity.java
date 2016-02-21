package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActiviy;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/20
 */
public class PersonIntroActivity extends BaseSwipeActivity {
    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;
    @Bind(R.id.edit_person_intro_et)
    EditText mPersonIntroEt;

    public final static String EXTRA_PERSON_INTRO="person_intro";

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_person_intro;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        initToolBars();

        String personIntro= (getIntent().getExtras()==null)?null:getIntent().getExtras().getString(EXTRA_PERSON_INTRO);
        mPersonIntroEt.setText(personIntro);
        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_PERSON_INTRO, mPersonIntroEt.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(EditInformationActiviy.REQUESTCODE_Person_Intro, intent);
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
        mToolbarTv.setText(R.string.person_intro);
        mToolbarRightTv.setVisibility(View.VISIBLE);
        mToolbarRightTv.setText(R.string.keep);
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PERSON_INTRO, mPersonIntroEt.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(EditInformationActiviy.REQUESTCODE_Person_Intro, intent);
        super.onBackPressed();
    }
}
