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

package com.umeng.comm.ui.activities;

import android.support.v4.app.Fragment;
import android.view.View;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.fragments.AtMeFeedFragment;
import com.umeng.common.ui.activities.NewMsgBaseActivity;
import com.umeng.common.ui.fragments.CommentTabFragment;
import com.umeng.common.ui.fragments.LikedMeFragment;
import com.umeng.common.ui.fragments.MessageSessionFragment;
import com.umeng.common.ui.fragments.NotificationFragment;


public class NewMsgActivity extends NewMsgBaseActivity {

    @Override
    public void initParams() {
        mDots = new View[5];
        mFragments = new Fragment[5];
    }

    @Override
    public void settingBtnViews() {
        String comment = ResFinder.getString("umeng_comm_comment");
        initBtnView(ResFinder.getId("umeng_comm_my_msg_comment"), comment,
                "umeng_comm_msg_comment", 0, true);

        String like = ResFinder.getString("umeng_comm_like");
        initBtnView(ResFinder.getId("umeng_comm_my_msg_like"), like,
                "umeng_comm_msg_like", 1, true);

        String notification = ResFinder.getString("umeng_comm_notify");
        initBtnView(ResFinder.getId("umeng_comm_my_msg_notice"), notification,
                "umeng_comm_msg_notice", 2, true);

        String privateLetter = ResFinder.getString("umeng_comm_private_letter");
        initBtnView(ResFinder.getId("umeng_comm_my_msg_private_letter"), privateLetter,
                "umeng_comm_msg_chat", 3, false);

        String atMe = ResFinder.getString("umeng_comm_at_me");
        initBtnView(ResFinder.getId("umeng_comm_my_msg_at"), atMe,
                "umeng_comm_msg_like", 4, true);
    }

    @Override
    public void onBtnViewClick(int position) {
        showFragment(position);
    }

    private void showFragment(int position) {
        if (mFragments[position] == null) {
            Fragment f = null;
            switch (position) {
                case 0:
                    f = new CommentTabFragment();
                    ((CommentTabFragment)f).setUserInfoClassName(UserInfoActivity.class);
                    ((CommentTabFragment)f).setTopicDetailClassName(TopicDetailActivity.class);
                    ((CommentTabFragment)f).setFeedDetailClassName(FeedDetailActivity.class);
                    break;

                case 1:
                    f = new LikedMeFragment();
                    ((LikedMeFragment)f).setUserInfoClassName(UserInfoActivity.class);
                    ((LikedMeFragment)f).setTopicDetailClassName(TopicDetailActivity.class);
                    ((LikedMeFragment)f).setFeedDetailClassName(FeedDetailActivity.class);
                    break;

                case 2:
                    f = new NotificationFragment();
                    ((NotificationFragment) f).setUserInfoClassName(UserInfoActivity.class);
                    break;

                case 3:
                    f = new MessageSessionFragment();
                    break;

                case 4:
                    f = new AtMeFeedFragment();
                    break;

                default: break;
            }
            mFragments[position] = f;
        }
        if (mFragments[position] != null) {
            showFragment(mFragments[position]);
        }
        mFragmentContainerView.setVisibility(View.VISIBLE);
        mBtnContainerView.setVisibility(View.GONE);
    }
}
