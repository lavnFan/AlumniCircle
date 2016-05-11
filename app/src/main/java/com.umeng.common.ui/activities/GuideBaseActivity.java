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

package com.umeng.common.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.umeng.comm.core.utils.ResFinder;


/**
 * 用户首次注册成功并修改用户信息后，将进行话题跟活跃用户的引导
 */
public abstract class GuideBaseActivity extends FragmentActivity {
    protected FragmentManager mFragmentManager = null;
    protected int mContainer = 0;
    /**
     * 当前显示的Fragment
     */
    public Fragment mCurrentFragment;
    /**
     * Fragment的parent view,即Fragment的容器
     */
    protected int mFragmentContainer;
    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        mFragmentManager = getSupportFragmentManager();
        setContentView(ResFinder.getLayout("umeng_comm_guide_activity"));
        mContainer = ResFinder.getId("umeng_comm_guide_container");
        showTopicFragment();
    }

    /**
     * 显示话题引导页面</br>
     */
//    protected void showTopicFragment() {
//        RecommendTopicBaseFragment topicRecommendDialog =RecommendTopicBaseFragment.newRecommendTopicFragment();
//        topicRecommendDialog.setOnDismissListener(new OnDismissListener() {
//
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                showRecommendUserFragment();
//            }
//        });
//        addFragment(mContainer, topicRecommendDialog);
//    }
    protected abstract  void showTopicFragment();
    /**
     * 判断一个Fragment是否已经添加
     *
     * @param fragment 要判断是否已经添加的Fragment
     * @return
     */
    public boolean isFragmentAdded(Fragment fragment) {
        return fragment != null
                && mFragmentManager.findFragmentByTag(fragment.getClass().getName()) != null;
    }
    /**
     * @param container 用于放置fragment的布局id
     * @param fragment 要添加的Fragment
     */
    public void addFragment(int container, Fragment fragment) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (!isFragmentAdded(fragment)) {
            fragmentTransaction
                    .add(container, fragment,
                            fragment.getClass().getName()).commitAllowingStateLoss();
            mCurrentFragment = fragment;
        } else {
            fragmentTransaction.show(fragment).commitAllowingStateLoss();
        }

        mFragmentContainer = container;
    }
//    protected void showRecommendUserFragment() {
//        setFragmentContainerId(mContainer);
//        RecommendUserFragment recommendUserFragment = new RecommendUserFragment();
//        replaceFragment(mContainer, recommendUserFragment);
//    }
protected abstract void showRecommendUserFragment();
    /**
     * 需要在调用任何函数前设置
     *
     * @param container 用于放置fragment的布局id
     */
    public void setFragmentContainerId(int container) {
        mFragmentContainer = container;
    }
    /**
     * 移除上一个Fragment，显示传递进来的Fragment
     *
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false);
    }

    /**
     * @param fragment
     */
    public void replaceFragment(Fragment fragment, boolean isAddToBackStack) {
        checkContainer();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mFragmentContainer,
                fragment);
        if (isAddToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
        mCurrentFragment = fragment;
    }

    /**
     * @param container
     * @param fragment
     */
    public void replaceFragment(int container, Fragment fragment) {
        checkContainer();
        if (mCurrentFragment != fragment) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(container,
                    fragment, fragment.getClass().getSimpleName());
            transaction.commitAllowingStateLoss();
            mCurrentFragment = fragment;
        }
    }
    /**
     * 检查放置fragment的布局id
     */
    protected void checkContainer() {
        if (mFragmentContainer <= 0) {
            throw new RuntimeException(
                    "在调用replaceFragment函数之前请调用setFragmentContainerId函数来设置fragment container id");
        }
    }
}
