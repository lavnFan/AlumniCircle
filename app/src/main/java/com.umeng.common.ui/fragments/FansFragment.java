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

package com.umeng.common.ui.fragments;

import android.os.Bundle;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.FansFgPresenter;
import com.umeng.common.ui.presenter.impl.FollowedUserFgPresenter;

import java.util.List;

/**
 * 粉丝页面
 */
public class FansFragment extends FollowedUserFragment {

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_fans"));
    }

    @Override
    protected FollowedUserFgPresenter createPresenters() {
        String uid = getArguments().getString(Constants.USER_ID_KEY);
        return new FansFgPresenter(this, uid);
    }

    public static FansFragment newFansFragment(String uid) {
        FansFragment fansFragment = new FansFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID_KEY, uid);
        fansFragment.mUserId = uid;
        fansFragment.setArguments(bundle);
        return fansFragment;
    }

    @Override
    public void updateFollowedState(String uId, boolean followedState) {
        super.updateFollowedState(uId, followedState);
        if(mUserId.equals(uId)){
            List<CommUser> data = mAdapter.getDataSource();
            if(followedState){
                data.remove(CommConfig.getConfig().loginedUser);
                data.add(0,CommConfig.getConfig().loginedUser);
            }else{
                data.remove(CommConfig.getConfig().loginedUser);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
