package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.NewFriendsPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.INewFriendsView;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsFriendsItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;
import com.umeng.comm.core.beans.CommUser;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class NewFriendsActivity extends BaseSwipeActivity implements INewFriendsView {
    @Bind(R.id.contacts_new_friends_lm_list_view)
    ScrollLoadMoreListView mListView;
    @Bind(R.id.contacts_new_friends_scroll_view)
    ScrollView mScrollView;

    private ContactsFriendsItemAdapter mAdapter;
    @Inject
    NewFriendsPresenter newFriendsPresenter;
    private boolean isFirst = true;

    @Override
    protected int getContentView() {
        return R.layout.activity_contacts_new_friends;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        newFriendsPresenter.attachView(this);
        setToolbarTitle(getResources().getString(R.string.new_friend));
    }

    @Override
    protected void initViewsAndEvents() {
        mScrollView.smoothScrollTo(0, 0);
        newFriendsPresenter.init();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void init(final List<FriendRequestItem> friendRequestItems) {
//        if (friendRequestItems.size() != 0) {
        List<FriendRequestItem> entities = friendRequestItems;
        mAdapter = new ContactsFriendsItemAdapter(this);
        mAdapter.setmEntities(entities);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setFriendMessage(new ContactsFriendsItemAdapter.FriendMessage() {
            @Override
            public void acceptRequest(String user_id, Button sendBtn) {     //接受好友申请
                newFriendsPresenter.acceptFriendRequest(user_id, sendBtn);
            }

            @Override
            public void deleteRequest(String user_id) {
                newFriendsPresenter.deleteFriendRequest(user_id);
            }
        });

        if (isFirst && friendRequestItems.size() == 0) {
            toggleShowEmpty(true, null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            isFirst = false;
        }
    }

    @Override
    public void deleteSuccess() {
        newFriendsPresenter.init();
        showToast("删除成功");
    }

    @Override
    public void acceptSuccess(Button sendBtn) {
        newFriendsPresenter.init();
        showToast("添加成功");
        //发送广播，更新好友列表
        Intent intent = new Intent();
        intent.setAction("refresh_friends_list");
        sendBroadcast(intent);
    }

    @OnClick(R.id.contacts_new_friends_search_ll)
    void search() {
        readyGo(SearchFriendActivity.class);
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
}
