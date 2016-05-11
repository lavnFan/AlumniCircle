package com.umeng.comm.ui.presenter.impl;

import android.content.Intent;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.ui.activities.ForwardActivity;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;

import java.util.List;

/**
 * Created by wangfei on 16/1/18.
 */
public class BaseFeedWeiboPresenter  extends BaseFragmentPresenter<List<FeedItem>> {

    /**
     * 跳转到转发页面</br>
     */
    //// TODO: 16/1/18 需不需要抽象？
    public void gotoForwardActivity(FeedItem item) {
        Intent intent = new Intent(mContext,
                ForwardActivity.class);
        intent.putExtra(Constants.FEED, item);
        mContext.startActivity(intent);
    }

    /**
     * 保存新加载的数据。如果该数据存在于DB中，则替换成最新的，否则Insert一条新纪录.
     *
     * @param newFeedItems
     */
    @Override
    protected void saveDataToDB(List<FeedItem> newFeedItems) {
        mDatabaseAPI.getFeedDBAPI().saveFeedsToDB(newFeedItems);
    }
}
