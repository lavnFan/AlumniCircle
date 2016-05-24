package com.seu.wufan.alumnicircle.ui.activity.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.SearchFriendItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.Constant;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.model.TokenModel;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.SearchPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.ISearchView;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.SearchFriendAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.SearchHistoryAdapter;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.ui.activities.UserInfoActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author wufan
 * @date 2016/5/21
 */
public class SearchFriendActivity extends BaseSwipeActivity implements ISearchView {

    @Bind(R.id.search_text)
    EditText mSearchText;
    @Bind(R.id.search_img)
    ImageView mSearchImg;
    @Bind(R.id.search_history_list)
    ListView mHistoryList;
    @Bind(R.id.search_history_delete_linear)
    LinearLayout mDeleteLinear;
    @Bind(R.id.search_history_linear)
    LinearLayout mHistoryLl;
    @Bind(R.id.search_friends_list)
    ListView mFriendsList;

    private SearchFriendAdapter friendAdapter;
    private SearchHistoryAdapter historyAdapter;
    private String keyWord = null;
    private List<SearchFriendItem> searchFriendItems = new ArrayList<>();

    @Inject
    SearchPresenter searchPresenter;

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        searchPresenter.attachView(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_friends;
    }

    @Override
    protected void initViewsAndEvents() {
        showSearchHistory();

        mFriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommUser user = new CommUser();
                user.sourceUid = searchFriendItems.get(position).getUser_id();
                if (searchFriendItems.get(position).getIs_master().equals("1")) { //若是校友大咖，则进入个人关注页面
                    Intent intent = new Intent(SearchFriendActivity.this,
                            UserInfoActivity.class);
                    intent.putExtra(Constants.TAG_USER, user);
                    startActivity(intent);
                } else {        //否则，进入个人发消息/加好友界面
                    Intent intent = new Intent(SearchFriendActivity.this, MyInformationActivity.class);
                    intent.putExtra(Constants.TAG_USER, user);
                    startActivity(intent);
                }

            }
        });
        mHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchPresenter.search(Constant.searchHistory.get(position));
            }
        });
    }

    @Override
    protected View getLoadingTargetView() {
        return mHistoryLl;
    }

    @OnClick(R.id.search_history_delete_linear)
    void deleteHistoryLl() {
        Constant.searchHistory = new ArrayList<>();
        showSearchHistory();
    }

    @OnClick(R.id.search_text)
    void searchText() {
        //点击后显示历史记录，若没有则不显示
        TLog.i("TAG", "click the text ");
        if (Constant.searchHistory.size() != 0) {
            showSearchHistory();
        }
    }

    private void showSearchHistory() {
        mFriendsList.setVisibility(View.GONE);
        mHistoryLl.setVisibility(View.VISIBLE);
        List<String> hisotrys = new ArrayList<>();
        List<String> temp = Constant.searchHistory;
        int n = temp.size() > 10 ? 10 : temp.size();
        for (int i = 0; i < n; i++) {
            hisotrys.add(temp.get(n - i - 1));
        }
        historyAdapter = new SearchHistoryAdapter(this, hisotrys, SearchHistoryAdapter.ViewHolder.class);
        mHistoryList.setAdapter(historyAdapter);
    }

    @OnClick(R.id.search_img)
    void search() {
        keyWord = mSearchText.getText().toString();
        if (keyWord != null) {
            searchPresenter.search(keyWord);
        } else {
            showToast("搜索关键字不能为空!");
        }
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
    public void showUserResult(List<SearchFriendItem> searchFriendItems) {
        if (searchFriendItems.size()!=0) {
            this.searchFriendItems = searchFriendItems;
            mHistoryLl.setVisibility(View.GONE);
            mFriendsList.setVisibility(View.VISIBLE);
            Constant.searchHistory.add(keyWord);
            if (friendAdapter == null) {
                friendAdapter = new SearchFriendAdapter(this, searchFriendItems, SearchFriendAdapter.ViewHolder.class);
                mFriendsList.setAdapter(friendAdapter);
            } else {
                friendAdapter.setmEntities(searchFriendItems);
                friendAdapter.notifyDataSetChanged();
            }
        }else{
            toggleShowEmpty(true, null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
