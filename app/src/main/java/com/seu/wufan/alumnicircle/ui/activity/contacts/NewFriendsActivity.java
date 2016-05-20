package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.FriendRequestItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.NewFriendsPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.INewFriendsView;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsFriendsItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.ScrollLoadMoreListView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/12
 */
public class NewFriendsActivity extends BaseSwipeActivity implements INewFriendsView{
    @Bind(R.id.contacts_new_friends_lm_list_view)
    ScrollLoadMoreListView mListView;
    @Bind(R.id.contacts_new_friends_scroll_view)
    ScrollView mScrollView;

    private ContactsFriendsItemAdapter mAdapter;
    @Inject
    NewFriendsPresenter newFriendsPresenter;

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
        mScrollView.smoothScrollTo(0,0);
        newFriendsPresenter.init();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void init(List<FriendRequestItem> friendRequestItems) {
        List<FriendRequestItem> entities = friendRequestItems;
        mAdapter = new ContactsFriendsItemAdapter(this);
        mAdapter.setmEntities(entities);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setFriendMessage(new ContactsFriendsItemAdapter.FriendMessage() {
            @Override
            public void acceptRequest(String user_id, Button sendBtn) {     //接受好友申请
                newFriendsPresenter.acceptFriendRequest(user_id,sendBtn);
            }

            @Override
            public void deleteRequest(String user_id) {
                newFriendsPresenter.deleteFriendRequest(user_id);
            }
        });
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
        ToastUtils.showToast(s,this);
    }
}
