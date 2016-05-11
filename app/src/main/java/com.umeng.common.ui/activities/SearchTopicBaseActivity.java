package com.umeng.common.ui.activities;

import android.os.Bundle;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.fragments.BaseFragment;


/**
 * Created by wangfei on 16/1/19.
 */
public abstract class SearchTopicBaseActivity<T extends BaseFragment> extends BaseFragmentActivity {
    protected T mSearchFragment;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(ResFinder.getLayout("umeng_comm_search_activity"));
        mSearchFragment = createSearchTopicFragment();
        int container = ResFinder.getId("umeng_comm_main_container");
        setFragmentContainerId(container);
        showFragmentInContainer(container, mSearchFragment);
    }
    protected abstract T createSearchTopicFragment();
}
