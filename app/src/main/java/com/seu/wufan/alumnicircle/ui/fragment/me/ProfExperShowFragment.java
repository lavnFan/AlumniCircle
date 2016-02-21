package com.seu.wufan.alumnicircle.ui.fragment.me;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.fragment.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/16
 */
public class ProfExperShowFragment extends BaseFragment {

    ProfExperEditFragment editFragment;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_edit_prof_exper_show;
    }

    @Override
    public void initViews(View view) {

    }

    @Override
    public void initDatas() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @OnClick(R.id.me_edit_info_prof_exper_show_linear_layout)
    public void onClick() {
        if(editFragment==null){
            editFragment = new ProfExperEditFragment();
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_prof_exper_container,editFragment).addToBackStack(null).commit();
    }
}
