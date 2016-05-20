package com.seu.wufan.alumnicircle.ui.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
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
import com.seu.wufan.alumnicircle.api.entity.item.FriendListItem;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.contacts.ContactsPresenter;
import com.seu.wufan.alumnicircle.mvp.views.fragment.IContactsView;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.activity.contacts.AlumniGoodActivity;
import com.seu.wufan.alumnicircle.ui.activity.contacts.NewFriendsActivity;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.contacts.ContactsItemAdapter;
import com.seu.wufan.alumnicircle.common.base.BaseLazyFragment;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.FriendsManager;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.LeanchatUser;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.CharacterParser;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.PinyinComparator;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.SideBar;

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
public class ContactsFragment extends BaseLazyFragment implements View.OnClickListener,IContactsView {

    @Bind(R.id.contacts_list_view)
    ListView mListView;
    @Bind(R.id.contacts_dialog_tv)
    TextView mDialogTv;
    @Bind(R.id.contacts_sidrbar)
    SideBar mSidBar;

    @Inject
    ContactsPresenter contactsPresenter;

    private List<Friend> dataList = new ArrayList<>();
    private List<Friend> sourceDataList = new ArrayList<Friend>();

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
                Intent intent = new Intent(getActivity(), AVChatActivity.class);
                intent.putExtra(Constants.MEMBER_ID, sourceDataList.get(position-1).getName());
                getActivity().startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);
        getMembers(false);
    }

    private void getMembers(final boolean isforce) {
        FriendsManager.fetchFriends(isforce, new FindCallback<LeanchatUser>() {
            @Override
            public void done(List<LeanchatUser> list, AVException e) {
//                refreshLayout.setRefreshing(false);
//                itemAdapter.setUserList(list);
//                itemAdapter.notifyDataSetChanged();
            }
        });
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
        ToastUtils.showToast(s,getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contactsPresenter.destroy();
        EventBus.getDefault().unregister(this);
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

        adapter = new ContactsAdapter(getActivity());

        mListView.addHeaderView(headView);
        mListView.setAdapter(adapter);
        adapter.setmEntities(sourceDataList);
        adapter.notifyDataSetChanged();
    }
}
