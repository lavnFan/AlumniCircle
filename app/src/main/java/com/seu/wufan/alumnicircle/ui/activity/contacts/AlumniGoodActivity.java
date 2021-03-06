package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ScrollView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.ContactsFriendsItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsAlumniGoodItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;
import com.umeng.comm.ui.fragments.ActiveUserFragment;
import com.umeng.comm.ui.fragments.RecommendUserFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class AlumniGoodActivity extends BaseSwipeActivity {

//    @Bind(R.id.contacts_alumni_good_lm_list_view)
//    ScrollLoadMoreListView mListView;
//    @Bind(R.id.contacts_alumni_good_scroll_view)
//    ScrollView mScrollView;
//    private BasisAdapter mAdapter;
    RecommendUserFragment recommendUserFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_contacts_alumni_good;
    }

    @Override
    protected void prepareDatas() {
        setToolbarTitle(getResources().getString(R.string.alumni_good));
    }

    @Override
    protected void initViewsAndEvents() {
        if(recommendUserFragment==null){
            recommendUserFragment = new RecommendUserFragment();
            recommendUserFragment.setSaveButtonInvisiable();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contacts_alumni_good_container, recommendUserFragment).commit();
        }

//        mScrollView.smoothScrollTo(0,0);
//        List<ContactsFriendsItem> entities = new ArrayList<ContactsFriendsItem>();
//        for (int i = 0; i < 10; i++) {
//            entities.add(new ContactsFriendsItem());
//        }
//        mAdapter = new ContactsAlumniGoodItemAdapter(this);
//        mAdapter.setmEntities(entities);
//        mListView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
