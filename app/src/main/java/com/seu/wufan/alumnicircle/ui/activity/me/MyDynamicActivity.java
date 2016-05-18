package com.seu.wufan.alumnicircle.ui.activity.me;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ScrollView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.circle.DynamicItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.ui.fragments.PostedFeedsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyDynamicActivity  extends BaseSwipeActivity {

//    @Bind(R.id.my_dynamic_scroll_view)
//    ScrollView mScrollView;

    PostedFeedsFragment postedFeedsFragment;

    public static final String EXTRA_COMM_USER="comm_user";
    CommUser user = new CommUser();

    @Override
    protected int getContentView() {
        return R.layout.activity_my_dynamic;
    }

    @Override
    protected void prepareDatas() {
        setToolbarTitle(getResources().getString(R.string.my_dynamic));
        user = getIntent().getExtras().getParcelable(EXTRA_COMM_USER);
    }

    @Override
    protected void initViewsAndEvents() {
//        mScrollView.smoothScrollTo(0,0);
        if(postedFeedsFragment==null){
            postedFeedsFragment = PostedFeedsFragment.newInstance();
            if(user!=null){
                postedFeedsFragment.setCurrentUser(user);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.my_dynamic_container, postedFeedsFragment).commit();
        }
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

}