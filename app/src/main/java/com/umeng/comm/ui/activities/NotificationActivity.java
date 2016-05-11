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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.activities.BaseFragmentActivity;
import com.umeng.common.ui.fragments.NotificationFragment;


/**
 * @author mrsimple
 */
public class NotificationActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(ResFinder.getLayout("umeng_comm_notify_activity"));
        initTitleLayout();
        attachFragment();
    }

    private void initTitleLayout() {
        findViewByIdWithFinder(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        TextView textView = findViewByIdWithFinder(ResFinder.getId("umeng_comm_setting_title"));
        textView.setText(ResFinder.getString("umeng_comm_admin_notify"));
    }

    private void attachFragment() {
        NotificationFragment fragment = new NotificationFragment();
        fragment.setUserInfoClassName(UserInfoActivity.class);
        addFragment(ResFinder.getId("umeng_comm_notify_fragment_layout"), fragment);
    }
}
