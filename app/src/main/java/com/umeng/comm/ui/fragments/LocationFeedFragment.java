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
package com.umeng.comm.ui.fragments;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.FriendFeedPresenter;
import com.umeng.common.ui.presenter.impl.LocationFeedPresenter;


/**
 * 展示某个位置的Feed Fragment
 */
public class LocationFeedFragment extends FriendsFragment {

    private FeedItem mFeedItem;

    public LocationFeedFragment() {
    }

    @Override
    protected FriendFeedPresenter createPresenters() {
        return new LocationFeedPresenter(this, mFeedItem.location);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        TextView titleTextView = (TextView) mRootView.findViewById(ResFinder
                .getId("umeng_comm_title_tv"));
        titleTextView.setText(mFeedItem.locationAddr);

        // 处理返回事件
        mRootView.findViewById(ResFinder.getId("umeng_comm_title_back_btn")).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });

    }

    public static LocationFeedFragment newLocationFeedFragment(FeedItem feedItem) {
        LocationFeedFragment locationFeedFragment = new LocationFeedFragment();
        locationFeedFragment.mFeedItem = feedItem;
        return locationFeedFragment;
    }
    
    @Override
    protected void initAdapter() {
        super.initAdapter();
        mFeedLvAdapter.setShowDistance();
    }

}
