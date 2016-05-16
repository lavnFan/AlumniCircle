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
import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Edus;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.me.edit.EduShowPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.me.IEduShowView;
import com.seu.wufan.alumnicircle.ui.activity.me.EditInformationActivity;
import com.seu.wufan.alumnicircle.ui.fragment.me.EditFragmentToActivityListener;
import com.seu.wufan.alumnicircle.ui.fragment.me.EducationEditFragment;
import com.seu.wufan.alumnicircle.ui.fragment.me.EducationShowFragment;
import com.seu.wufan.alumnicircle.ui.fragment.me.ShowEduFragmentToActivityListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/5/14
 */
public class EducationActivity extends BaseSwipeActivity implements IEduShowView,ShowEduFragmentToActivityListener,EditFragmentToActivityListener {

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;
    @Bind(R.id.text_toolbar_right_tv)
    TextView mToolbarRightTv;

    EducationEditFragment editFragment;
    EducationShowFragment showFragment;

    private List<Edu> eduList = new ArrayList<>();
    private Edus edus = new Edus();
    public static final String EXTRA_EDU = "edu";
    public static final String EXTRA_EDU_ID = "edu_id";
    public static final String EXTRA_EDU_LIST = "edu_list";
    public static final int REQUEST_UPDATE = 1;
    public static final int REQUEST_ADD = 2;
    public static final int REQUEST_DELETE = 3;

    @Inject
    EduShowPresenter eduShowPresenter;

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        eduShowPresenter.attachView(this);
        UserInfoDetailRes userInfoDetailRes = (UserInfoDetailRes) getIntent().getExtras().getSerializable(EXTRA_EDU);
        eduList = userInfoDetailRes.getEduHistory();
        edus.setEdus(eduList);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_education;
    }

    @Override
    protected void initViewsAndEvents() {
        showFragment = EducationShowFragment.newInstance(edus);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_edu_container, showFragment).commit();

        initToolBars();

        mToolbarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFragment.keepEdu(new EducationEditFragment.KeepCallBack() {
                    @Override
                    public void keepEdu(Edu edu) {     //回调editFragment中的保存事件，再回退fragment
                        if (edu.getId() != null) {
                            eduShowPresenter.updateEdu(edu.getId(), edu);
                        } else {
                            eduShowPresenter.saveEdu(edu);
                        }
                    }
                });
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

    @Override
    public void replaceFragment(Edu edu) {
        editFragment = EducationEditFragment.newInstance(edu);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.edit_edu_container, editFragment).addToBackStack(null).commit();
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
    public void backEdit(Edu edu, int REQUEST_CODE) {   //presenter通知视图层更新,根据是否为添加还是修改
        //1、先回退EditFragment
        backFragment();
        //2、再通知showFragment进行视图更新
        edus = showFragment.updateShow(edu, REQUEST_CODE);
    }

    @Override
    public void backEditFragment(int REQUEST_CODE) {      //EditFragment调用的接口，点击删除时进行回退
        edus = showFragment.updateShow(null, REQUEST_CODE);
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
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_EDU_LIST, edus);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(EditInformationActivity.REQUESTCODE_EDU_HISTORY, intent);

        super.onBackPressed();
    }
}
