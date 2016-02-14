package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.view.View;
import android.widget.ScrollView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.model.item.ContactsFriendsItem;
import com.seu.wufan.alumnicircle.model.item.DynamicAgreeItem;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseActivity;
import com.seu.wufan.alumnicircle.ui.activity.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.ui.adapter.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsFriendsItemAdapter;
import com.seu.wufan.alumnicircle.ui.view.LoadMoreListView;
import com.seu.wufan.alumnicircle.ui.view.ScrollLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class NewFriendsActivity extends BaseSwipeActivity {
    @Bind(R.id.contacts_new_friends_lm_list_view)
    ScrollLoadMoreListView mListView;
    @Bind(R.id.contacts_new_friends_scroll_view)
    ScrollView mScrollView;

    private BasisAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_contacts_new_friends;
    }

    @Override
    protected void prepareDatas() {

    }

    @Override
    protected void initViews() {
        mScrollView.smoothScrollTo(0,0);
        List<ContactsFriendsItem> entities = new ArrayList<ContactsFriendsItem>();
        for (int i = 0; i < 10; i++) {
            entities.add(new ContactsFriendsItem());
        }
        mAdapter = new ContactsFriendsItemAdapter(this);
        mAdapter.setmEntities(entities);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }
}
