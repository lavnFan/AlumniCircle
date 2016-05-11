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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.fragments.MessageChatFragment;


/**
 * @author mrsimple
 */
public class MessageChatActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(ResFinder.getLayout("umeng_commm_notify_activity"));
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
        String userName = getIntent().getStringExtra("userName");
        textView.setText(TextUtils.isEmpty(userName) ? "" : userName);
    }

    private void attachFragment() {
        String uId = getIntent().getStringExtra("uid");
        addFragment(ResFinder.getId("umeng_comm_notify_fragment_layout"),
                MessageChatFragment.newMessageChatFragment(uId));
    }
}
