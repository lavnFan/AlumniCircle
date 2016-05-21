package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.view.View;
import android.widget.TextView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class AddFriendsActivity extends BaseSwipeActivity {

    @Bind(R.id.text_toolbar_tv)
    TextView mToolbarTv;

    @Override
    protected int getContentView() {
        return R.layout.activity_contacts_add_friends;
    }

    @Override
    protected void prepareDatas() {
        setToolbarTitle(getResources().getString(R.string.new_friend));
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    private void initToolBars() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTv.setVisibility(View.VISIBLE);
        mToolbarTv.setText(R.string.add_friend_);
    }

    @OnClick({R.id.contacts_add_friends_phone_ll, R.id.contacts_add_friends_alumni_ll, R.id.contacts_add_friends_scan_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contacts_add_friends_phone_ll:
                break;
            case R.id.contacts_add_friends_alumni_ll:
                break;
            case R.id.contacts_add_friends_scan_ll:
                break;
        }
    }
}
