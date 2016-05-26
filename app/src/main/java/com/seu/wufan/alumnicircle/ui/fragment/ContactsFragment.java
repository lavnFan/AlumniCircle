package com.seu.wufan.alumnicircle.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.leanchatlib.activity.AVChatActivity;
import com.avoscloud.leanchatlib.utils.Constants;
import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.ContactsPresenter;
import com.seu.wufan.alumnicircle.mvp.views.fragment.IContactsView;
import com.seu.wufan.alumnicircle.ui.activity.contacts.AlumniGoodActivity;
import com.seu.wufan.alumnicircle.ui.activity.contacts.NewFriendsActivity;
import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsAdapter;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.FriendsManager;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.LeanchatUser;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.CharacterParser;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.PinyinComparator;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.SideBar;
import com.umeng.comm.core.beans.CommUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * @author wufan
 * @date 2016/1/31
 */
public class ContactsFragment extends BaseLazyFragment implements View.OnClickListener, IContactsView {

    @Bind(R.id.contacts_list_view)
    ListView mListView;
    @Bind(R.id.contacts_dialog_tv)
    TextView mDialogTv;
    @Bind(R.id.contacts_sidrbar)
    SideBar mSidBar;
    @Bind(R.id.contacts_swipe_fresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject
    ContactsPresenter contactsPresenter;

    private List<Friend> dataList = new ArrayList<>();
    private List<Friend> sourceDataList = new ArrayList<Friend>();
    private int deletePos = 0;

    private ContactsAdapter adapter;

    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private LayoutInflater infalter;
    View headView;

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {
        mDialogTv.setVisibility(View.INVISIBLE);
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void prepareData() {
        getApiComponent().inject(this);
        contactsPresenter.attachView(this);
        contactsPresenter.initLeanCloud();
    }

    @Override
    protected void initViewsAndEvents() {
        infalter = LayoutInflater.from(getActivity());
        headView = infalter.inflate(R.layout.list_item_contact_header,
                null);
        RelativeLayout re_newfriends = (RelativeLayout) headView.findViewById(R.id.contacts_new_friends_relative_layout);
        re_newfriends.setOnClickListener(this);
        RelativeLayout re_alumniGood = (RelativeLayout) headView.findViewById(R.id.contacts_alumni_good_relative_layout);
        re_alumniGood.setOnClickListener(this);
        mListView.addHeaderView(headView);

        adapter = new ContactsAdapter(getActivity());
        mListView.setAdapter(adapter);

        contactsPresenter.initFriendsList();

        mSidBar.setTextView(mDialogTv);
        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommUser user = new CommUser();
                user.sourceUid = sourceDataList.get(position - 1).getUser_id();
                Intent intent = new Intent(getActivity(),
                        MyInformationActivity.class);
                intent.putExtra(com.umeng.comm.core.constants.Constants.TAG_USER, user);
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String[] items = {"删除"};
                new AlertDialog.Builder(getActivity())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contactsPresenter.deleteFriend(sourceDataList.get(position-1).getUser_id());
                                deletePos = position-1;
                            }
                        }).create()
                        .show();
                return false;
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_theme_color));
//        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light, android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onPullDown();
            }
        });
    }

    private void onPullDown() {
        contactsPresenter.initLeanCloud();
        contactsPresenter.initFriendsList();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_contacts;
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
//    private void filterData(String filterStr) {
//        List<Friend> filterDateList = new ArrayList<Friend>();
//
//        if (TextUtils.isEmpty(filterStr)) {
//            filterDateList = sourceDataList;
//        } else {
//            filterDateList.clear();
//            for (Friend friendModel : sourceDataList) {
//                String name = friendModel.getName();
//                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
//                    filterDateList.add(friendModel);
//                }
//            }
//        }
//
//        // 根据a-z进行排序
//        Collections.sort(filterDateList, pinyinComparator);
//        adapter.updateListView(filterDateList);
//    }

    /**
     * 为名字添加排序的letter
     *
     * @param
     * @return
     */
    private List<Friend> filledData(List<Friend> lsit) {
        List<Friend> mFriendList = new ArrayList<Friend>();

        for (int i = 0; i < lsit.size(); i++) {
            Friend friendModel = new Friend();
            friendModel.setUser_id(lsit.get(i).getUser_id());
            friendModel.setName(lsit.get(i).getName());
            friendModel.setImage(lsit.get(i).getImage());

            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendModel.setLetters(sortString.toUpperCase());
            } else {
                friendModel.setLetters("#");
            }

            mFriendList.add(friendModel);
        }
        return mFriendList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_new_friends_relative_layout:
                readyGo(NewFriendsActivity.class);
                break;
            case R.id.contacts_alumni_good_relative_layout:
                readyGo(AlumniGoodActivity.class);
                break;
        }
    }

    @Override
    public void showNetCantUse() {
        ToastUtils.showNetCantUse(getActivity());
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(getActivity());
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s, getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contactsPresenter.destroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public void initFriendsList(List<Friend> friendListItems) {
        dataList = friendListItems;
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();

        if (dataList != null && dataList.size() > 0) {
            sourceDataList = filledData(dataList); //过滤数据为有字母的字段  现在有字母 别的数据没有
        }
        dataList = null; //释放资源

        // 根据a-z进行排序源数据
        Collections.sort(sourceDataList, pinyinComparator);
        adapter.setmEntities(sourceDataList);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void refreshfalse(boolean b) {
        mSwipeRefreshLayout.setRefreshing(b);
    }

    @Override
    public void refreshDelete() {
        sourceDataList.remove(deletePos);
        adapter.setmEntities(sourceDataList);
        adapter.notifyDataSetChanged();
    }

    BroadcastReceiver broadcastReceiver =new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            TLog.i("TAG","receive broadcast!");
            onPullDown();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //在当前activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh_friends_list");
        getActivity().registerReceiver(this.broadcastReceiver,filter);
    }
}
