package com.seu.wufan.alumnicircle.ui.activity.me.edit;

import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.fragment.me.ProfExperShowFragment;

/**
 * @author wufan
 * @date 2016/2/16
 */
public class ProfExperActivity extends BaseSwipeActivity{

    ProfExperShowFragment showFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_prof_exper;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViewsAndEvents() {
        if(showFragment==null){
            showFragment = new ProfExperShowFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_prof_exper_container,showFragment).commit();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
