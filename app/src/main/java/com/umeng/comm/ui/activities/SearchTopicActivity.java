package com.umeng.comm.ui.activities;


import android.view.KeyEvent;

import com.umeng.comm.ui.fragments.SearchTopicFragment;
import com.umeng.common.ui.activities.SearchTopicBaseActivity;


/**
 * Created by wangfei on 15/12/8.
 */
public class SearchTopicActivity extends SearchTopicBaseActivity<SearchTopicFragment> {


    @Override
    protected SearchTopicFragment createSearchTopicFragment() {
        return new SearchTopicFragment();
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {

            ((SearchTopicFragment)mSearchFragment).executeSearch();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
