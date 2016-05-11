/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.common.ui.presenter.impl;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;

import java.util.List;

public abstract class BaseFeedPresenter extends BaseFragmentPresenter<List<FeedItem>> {

    /**
     * 跳转到转发页面</br>
     */
    //// TODO: 16/1/18 需不需要抽象？ 
//    public void gotoForwardActivity(FeedItem item) {
//        Intent forwardIntent = new Intent(mContext, ForwardActivity.class);
//        forwardIntent.putExtra(Constants.FEED, item);
//        mContext.startActivity(forwardIntent);
//    }

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
