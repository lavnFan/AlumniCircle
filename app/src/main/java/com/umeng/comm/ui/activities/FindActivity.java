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

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.seu.wufan.alumnicircle.ui.activity.me.MyInformationActivity;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.MessageCount;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.fragments.FavoritesFragment;
import com.umeng.comm.ui.fragments.FriendsFragment;
import com.umeng.comm.ui.fragments.NearbyFeedFragment;
import com.umeng.comm.ui.fragments.RealTimeFeedFragment;
import com.umeng.comm.ui.fragments.RecommendTopicFragment;
import com.umeng.comm.ui.fragments.RecommendUserFragment;
import com.umeng.common.ui.activities.AlbumActivity;
import com.umeng.common.ui.activities.FindBaseActivity;
import com.umeng.common.ui.fragments.NearByUserFragment;


/**
 * 发现的Activity
 */
public class FindActivity extends FindBaseActivity implements OnClickListener {


    private RecommendTopicFragment mRecommendTopicFragment;
    private RecommendUserFragment mRecommendUserFragment;
    private FriendsFragment mFriendsFragment;
    private NearbyFeedFragment mNearbyFeedFragment;
    private FavoritesFragment mFavoritesFragment;
    private RealTimeFeedFragment mRealTimeFeedFragment;
    private NearByUserFragment mNearByUserFragment;
    private MessageCount mUnReadMsg;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

    }
    protected void getLayout() {
        setContentView(ResFinder.getLayout("umeng_comm_find_layout"));
    }
    @Override
    protected void gotoMyFollowActivity() {
        Intent intent = new Intent(FindActivity.this, FollowedTopicActivity.class);
        intent.putExtra(Constants.USER_ID_KEY, mUser.id);
        startActivity(intent);
    }

    @Override
    protected void gotoMyPicActivity() {
        Intent intent = new Intent(FindActivity.this, AlbumActivity.class);
        intent.putExtra(Constants.USER_ID_KEY, mUser.id);
        startActivity(intent);
    }



    protected void gotoNotificationActivity() {
        Intent intent = new Intent(FindActivity.this, NotificationActivity.class);
        intent.putExtra(Constants.USER, mUser);
        startActivity(intent);
    }

    protected void gotoFeedNewMsgActivity() {
        Intent intent = new Intent(FindActivity.this, NewMsgActivity.class);
        intent.putExtra(Constants.USER, mUser);
        startActivity(intent);
    }

    /**
     * 跳转到用户中心Activity</br>
     */
    protected void gotoUserInfoActivity() {
        Intent intent = new Intent(FindActivity.this, MyInformationActivity.class);
//        if (mUser == null || TextUtils.isEmpty(mUser.id)) {// 来自开发者外部调用的情况
            intent.putExtra(Constants.TAG_USER, CommConfig.getConfig().loginedUser);
//        } else {
//            intent.putExtra(Constants.TAG_USER, mUser);
//        }
        // intent.putExtra(Constants.TYPE_CLASS, mContainerClass); //
        // 设置页面需要此参数，由于个人中心设置被移到此页面，暂时不传递该参数
        startActivity(intent);
    }

    /**
     * 显示附件推荐Feed</br>
     */
    protected void showNearbyFeed() {
        if (mNearbyFeedFragment == null) {
            mNearbyFeedFragment = NearbyFeedFragment.newNearbyFeedRecommend();
            mNearbyFeedFragment.setOnResultListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mNearbyFeedFragment);
    }

    /**
     * 显示实时内容的Fragment</br>
     */
    protected void showRealTimeFeed() {
        if (mRealTimeFeedFragment == null) {
            mRealTimeFeedFragment = RealTimeFeedFragment.newRealTimeFeedRecommend();
            mRealTimeFeedFragment.setOnResultListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mRealTimeFeedFragment);
    }

    /**
     * 显示收藏Feed</br>
     */
    protected void showFavoritesFeed() {
        if (mFavoritesFragment == null) {
            mFavoritesFragment = FavoritesFragment.newFavoritesFragment();
            mFavoritesFragment.setOnResultListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mFavoritesFragment);
    }

    /**
     * 显示推荐话题的Dialog</br>
     */
    protected void showRecommendTopic() {
        if (mRecommendTopicFragment == null) {
            mRecommendTopicFragment = RecommendTopicFragment.newRecommendTopicFragment();
            mRecommendTopicFragment.setSaveButtonInVisiable();
            mRecommendTopicFragment.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mRecommendTopicFragment);
    }

    /**
     * 隐藏发现页面，显示fragment</br>
     * 
     * @param fragment
     */
    protected void showCommFragment(Fragment fragment) {
        findViewById(ResFinder.getId("umeng_comm_find_base")).setVisibility(View.GONE);
        int container = ResFinder.getId("container");
        findViewById(container).setVisibility(View.VISIBLE);
        setFragmentContainerId(container);
        showFragmentInContainer(container, fragment);
    }

    /**
     * 隐藏fragment，显示发现页面</br>
     */
    protected void showFindPage() {
        super.showFindPage();
        findViewById(ResFinder.getId("umeng_comm_find_base")).setVisibility(
                View.VISIBLE);
        findViewById(ResFinder.getId("container")).setVisibility(View.GONE);
    }

    /**
     * 显示朋友圈Fragment</br>
     */
    protected void showFriendsFragment() {
        if (mFriendsFragment == null) {
            mFriendsFragment = FriendsFragment.newFriendsFragment();
            mFriendsFragment.setOnResultListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mFriendsFragment);
    }

    /**
     * 显示推荐用户fragment</br>
     */
    protected void showRecommendUserFragment() {
        if (mRecommendUserFragment == null) {
            mRecommendUserFragment = new RecommendUserFragment();
            mRecommendUserFragment.setSaveButtonInvisiable();
            mRecommendUserFragment.setOnResultListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mRecommendUserFragment);
    }

    @Override
    protected void showNearByUser() {
        if (mNearByUserFragment == null) {
            mNearByUserFragment = NearByUserFragment.newNearbyUserFragment();
            mNearByUserFragment.setTargetClassName(UserInfoActivity.class.getName());
            mNearByUserFragment.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showFindPage();
                }
            });
        }
        showCommFragment(mNearByUserFragment);
    }
    
}
