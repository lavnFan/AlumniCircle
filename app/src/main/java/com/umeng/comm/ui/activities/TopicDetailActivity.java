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

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.ui.fragments.ActiveUserFragment;
import com.umeng.comm.ui.fragments.HotTopicFeedFragment;
import com.umeng.comm.ui.fragments.LatestTopicFeedFragment;
import com.umeng.comm.ui.fragments.RecommendTopicFeedFragment;
import com.umeng.comm.ui.fragments.TopicFeedFragment;
import com.umeng.common.ui.activities.TopicDetailBaseActivity;


/**
 * 话题详情页
 */
public class TopicDetailActivity extends TopicDetailBaseActivity {

    /**
     * 话题详情的Fragment
     */
    private TopicFeedFragment mDetailFragment;
    private ActiveUserFragment mActiveUserFragment;
    private HotTopicFeedFragment hotTopicFeedFragment;
    private RecommendTopicFeedFragment recommendTopicFeedFragment;
    private LatestTopicFeedFragment lastestTopicFeedFragment;




    @Override
    protected void initTitles() {
        mTitles = getResources().getStringArray(
                ResFinder.getResourceId(ResType.ARRAY, "umeng_comm_topic_detail_tabs"));
    }



    @Override
    protected int getLayout() {
         return ResFinder.getLayout("umeng_comm_topic_detail_layout");
    }



    @Override
    protected void gotoPostFeedActivity() {
        Intent postIntent = new Intent(TopicDetailActivity.this, PostFeedActivity.class);
        postIntent.putExtra(Constants.TAG_TOPIC, mTopic);
        startActivity(postIntent);
    }

    /**
     * 获取对应的Fragment。
     * 
     * @param pos
     * @return
     */
    protected Fragment getFragment(int pos) {
        if (pos == 0) {
            if (mDetailFragment == null) {
                mDetailFragment = TopicFeedFragment.newTopicFeedFrmg(mTopic);
            }
            mDetailFragment.setOnAnimationListener(mListener);
            return mDetailFragment;
        } else if (pos == 1) {
            if (lastestTopicFeedFragment == null) {
                lastestTopicFeedFragment = LatestTopicFeedFragment.newTopicFeedFrmg(mTopic);
            }
            lastestTopicFeedFragment.setOnAnimationListener(mListener);
            return lastestTopicFeedFragment;
        }else if (pos == 2) {
            if (recommendTopicFeedFragment == null) {
                recommendTopicFeedFragment = RecommendTopicFeedFragment.newTopicFeedFrmg(mTopic);
            }
            recommendTopicFeedFragment.setOnAnimationListener(mListener);
            return recommendTopicFeedFragment;
        }else if (pos == 3) {
            if (hotTopicFeedFragment == null) {
                hotTopicFeedFragment = HotTopicFeedFragment.newTopicFeedFrmg(mTopic);
            }
            hotTopicFeedFragment.setOnAnimationListener(mListener);
            return hotTopicFeedFragment;
        }else if (pos == 4) {
            if (mActiveUserFragment == null) {
                mActiveUserFragment = ActiveUserFragment.newActiveUserFragment(mTopic);
            }
            mActiveUserFragment.setOnAnimationListener(mListener);
            return mActiveUserFragment;
        }
        return null;
    }

    private OnResultListener mListener = new OnResultListener() {

        @Override
        public void onResult(int status) {
//            if (status == 1) {// dismiss
//                mCustomAnimator.startDismissAnimation(mHeaderView);
//            } else if (status == 0) { // show
//                mCustomAnimator.startShowAnimation(mHeaderView);
//            }
        }
    };





}
