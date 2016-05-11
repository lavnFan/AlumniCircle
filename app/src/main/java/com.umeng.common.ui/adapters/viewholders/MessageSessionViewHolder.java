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

package com.umeng.common.ui.adapters.viewholders;

import android.graphics.Color;
import android.widget.TextView;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.widgets.RoundImageView;
import com.umeng.common.ui.widgets.SquareImageView;


public class MessageSessionViewHolder extends ViewHolder {

    public RoundImageView mUserIcon;
    public TextView mUserName;
    public TextView mSessionContent;
    public SquareImageView mUserGrade;
    public TextView mDot;
    public TextView mSessionTime;

    @Override
    protected int getItemLayout() {
        return ResFinder.getLayout("umeng_comm_message_session_item");
    }

    @Override
    protected void initWidgets() {
        mUserIcon = findViewById(ResFinder.getId("umeng_comm_user_picture"));
        mUserIcon.setBackGroundColor(Color.parseColor("#f5f6fa"));
        mUserName = findViewById(ResFinder.getId("umeng_comm_user_name"));
        mSessionContent = findViewById(ResFinder.getId("umeng_comm_session_content"));
        mUserGrade = findViewById(ResFinder.getId("umeng_comm_user_grade"));
        mDot = findViewById(ResFinder.getId("umeng_comm_session_badge_view"));
        mSessionTime = findViewById(ResFinder.getId("umeng_comm_session_time"));
    }
}
