package com.umeng.comm.ui.activities;

import android.content.DialogInterface;

import com.umeng.comm.ui.fragments.RecommendTopicFragment;
import com.umeng.comm.ui.fragments.RecommendUserFragment;
import com.umeng.common.ui.activities.GuideBaseActivity;


/**
 * Created by wangfei on 16/1/25.
 */
public class GuideActivity extends GuideBaseActivity {
    @Override
    protected void showTopicFragment() {
                RecommendTopicFragment topicRecommendDialog =RecommendTopicFragment.newRecommendTopicFragment();
        topicRecommendDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showRecommendUserFragment();
            }

        });
        addFragment(mContainer, topicRecommendDialog);
    }

    @Override
    protected void showRecommendUserFragment() {
        setFragmentContainerId(mContainer);
        RecommendUserFragment recommendUserFragment = new RecommendUserFragment();
        replaceFragment(mContainer, recommendUserFragment);
    }
}
