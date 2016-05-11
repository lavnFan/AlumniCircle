package com.umeng.common.ui.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.adapters.SearchTopicAdapter;
import com.umeng.common.ui.mvpview.MvpRecyclerView;
import com.umeng.common.ui.mvpview.MvpSearchTopicFgView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;
import com.umeng.common.ui.presenter.impl.TopicSearchPresenter;
import com.umeng.common.ui.widgets.EmptyView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 16/1/19.
 */
public abstract   class SearchTopicBaseFragment  extends BaseFragment<List<Topic>, TopicSearchPresenter>
        implements MvpRecyclerView,MvpSearchTopicFgView {
    protected ArrayList<Topic> mTopicList = new ArrayList<Topic>();
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter<SearchTopicAdapter.TopicViewHolder> mTopicAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected EditText mSearchEditText;
    protected BaseFragmentPresenter mPresenter;
    protected android.os.Handler mSearchHandler;
    protected Runnable mSearchRunnable;
    protected View mBackButton;
    protected ProgressDialog mProgressDialog;
    protected String mTextBefore="";
    protected EmptyView emptyView;
    protected abstract int getFragmentLayout() ;
    @Override
    protected void initWidgets() {
        mBackButton = mViewFinder.findViewById(ResFinder.getId("umeng_comm_back"));
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("加载中...");
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mSearchHandler = new android.os.Handler();
        emptyView = findViewById(ResFinder.getId("umeng_comm_topic_empty"));
        emptyView.setShowText("umeng_comm_no_search_topic");
        mSearchEditText = mViewFinder.findViewById(ResFinder.getId("umeng_comm_topic_edittext"));
        mRecyclerView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_relative_user_recyclerView"));
        findViewById(ResFinder.getId("umeng_comm_topic_search")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TopicSearchPresenter) mPresenter).executeSearch(mSearchEditText.getText()
                        .toString().trim());
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int postition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (postition == mTopicList.size() - 1) {
                    ((TopicSearchPresenter) mPresenter).fetchNextPageData();
                }
            }
        });
        mTopicAdapter = new SearchTopicAdapter(mTopicList,getActivity());
        mRecyclerView.setAdapter(mTopicAdapter);
        setAdaptertoFetail();
//        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER && !mSearchEditText.getText().toString().equals(mTextBefore)) {
//                    ((TopicSearchPresenter) mPresenter).executeSearch(mSearchEditText.getText()
//                            .toString().trim());
//                    return true;
//                }
//                return false;
//            }
//        });
//        mSearchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(final CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s.toString())) {
//                    if (!mTextBefore.equals(s.toString())) {
//                        mSearchRunnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                ((TopicSearchPresenter) mPresenter).executeSearch(s.toString().trim());
//                            }
//                        };
//                        mSearchHandler.postDelayed(mSearchRunnable, 300);
//                    }
//                    mTextBefore = s.toString();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        showSoftKeyboard(mSearchEditText);
        IntentFilter intentFilter = new IntentFilter(Constants.TOPIC_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent!=null){
                    Topic topic = intent.getParcelableExtra("topic");
                    int index = mTopicList.indexOf(topic);
                    if (index>=0){

                        mTopicList.remove(index);
                        mTopicList.add(index,topic);
                        mTopicAdapter.notifyDataSetChanged();
                    }
                }
            }
        },intentFilter);

    }
    public void executeSearch(){
        String keyword = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastMsg.showShortMsgByResName("umeng_comm_topic_search_no_keyword");
            return;
        }
        ((TopicSearchPresenter) mPresenter).executeSearch(mSearchEditText.getText()
                .toString().trim());
    }
    @Override
    public void showFeedEmptyView() {
      emptyView.show();
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideFeedEmptyView() {
      emptyView.hide();
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    public abstract void setAdaptertoFetail();

    @Override
    protected TopicSearchPresenter createPresenters() {
        mPresenter = new TopicSearchPresenter(this,  mTopicList,this);
        return (TopicSearchPresenter) mPresenter;
    }


    @Override
    public void onDataSetChanged() {
        mTopicAdapter.notifyDataSetChanged();
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void showProgressFooter() {
        mProgressDialog.show();
    }

    @Override
    public void hideProgressFooter() {
        if(mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }


    @Override
    public void onRefreshStart() {

    }

    @Override
    public void onRefreshEnd() {

    }

}
