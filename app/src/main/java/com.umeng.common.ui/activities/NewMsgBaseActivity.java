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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.MessageCount;
import com.umeng.comm.core.utils.ResFinder;

public abstract class NewMsgBaseActivity extends BaseFragmentActivity implements OnClickListener {

    public MessageCount mUnreadMsg = CommConfig.getConfig().mMessageCount;

    private TextView mTitleView;

    public View[] mDots;
    public Fragment[] mFragments;

    public View mFragmentContainerView;
    public View mBtnContainerView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(ResFinder.getLayout("umeng_commm_my_msg_layout"));
        setFragmentContainerId(ResFinder.getId("umeng_comm_my_msg_fragment"));

        initParams();
        initTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // message session unread count
        updateDotsVisiable(3);
    }

    public abstract void initParams();

    public abstract void settingBtnViews();

    public abstract void onBtnViewClick(int id);

    /**
     * 初始化title</br>
     *
     */
    private void initTitle() {
        int backButtonResId = ResFinder.getId("umeng_comm_title_back_btn");
        findViewById(backButtonResId).setOnClickListener(this);

        int titleResId = ResFinder.getId("umeng_comm_title_tv");
        mTitleView = (TextView)findViewById(titleResId);
        mTitleView.setText("消息");

        mFragmentContainerView = findViewById(ResFinder.getId("umeng_comm_my_msg_fragment"));
        mFragmentContainerView.setVisibility(View.GONE);
        mBtnContainerView = findViewById(ResFinder.getId("umeng_comm_my_msg_item_content"));

        settingBtnViews();
    }

    public void initBtnView(int viewResId, final String btnStr, String iconResStr, final int id,
                            boolean hasDivideLine) {
        View v = findViewById(viewResId);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnViewClick(id);
                updateUnreadMsgCount(id);
                updateDotsVisiable(id);
                mTitleView.setText(btnStr);
            }
        });

        TextView tv = (TextView) v.findViewById(ResFinder.getId("umeng_comm_my_msg_text_view"));
        tv.setText(btnStr);

        ImageView iconImg = (ImageView) v.findViewById(ResFinder.getId("umeng_comm_my_msg_icon_view"));
        iconImg.setImageDrawable(ResFinder.getDrawable(iconResStr));

        mDots[id] = v.findViewById(ResFinder.getId("umeng_comm_my_msg_badge_view"));
        updateDotsVisiable(id);

        if(!hasDivideLine){
            v.findViewById(ResFinder.getId("umeng_comm_divide_line")).setVisibility(View.GONE);
        }
    }

    private void updateDotsVisiable(int position) {
        int unReadMsgCount = 0;
        if (mUnreadMsg != null) {
            switch (position) {
                case 0:
                    unReadMsgCount = mUnreadMsg.unReadCommentsCount;
                    break;

                case 1:
                    unReadMsgCount = mUnreadMsg.unReadLikesCount;
                    break;

                case 2:
                    unReadMsgCount = mUnreadMsg.unReadNotice;
                    break;

                case 3:
                    unReadMsgCount = mUnreadMsg.unReadSessionsCount;
                    break;

                case 4:
                    unReadMsgCount = mUnreadMsg.unReadAtCount;
                    break;

                default: break;
            }
        }
        mDots[position].setVisibility(unReadMsgCount > 0 ? View.VISIBLE : View.GONE);
    }

    private void updateUnreadMsgCount(int index) {
        switch (index) {
            case 0:
                mUnreadMsg.unReadCommentsCount = 0;
                break;

            case 1:
                mUnreadMsg.unReadLikesCount = 0;
                break;

            case 2:
                mUnreadMsg.unReadNotice = 0;
                break;

            case 3:

                break;

            case 4:
                mUnreadMsg.unReadAtCount = 0;
                break;

            default: break;
        }
        mUnreadMsg.unReadTotal = mUnreadMsg.unReadAtCount + mUnreadMsg.unReadCommentsCount
                + mUnreadMsg.unReadLikesCount + mUnreadMsg.unReadNotice +
                mUnreadMsg.newFansCount + mUnreadMsg.unReadSessionsCount;
    }


    @Override
    public void onClick(View v) {
        if(!hideFragmentContainerView()){
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && hideFragmentContainerView()){
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private boolean hideFragmentContainerView(){
        if(mFragmentContainerView.getVisibility() == View.VISIBLE){
            mFragmentContainerView.setVisibility(View.GONE);
            mBtnContainerView.setVisibility(View.VISIBLE);
            mTitleView.setText("消息");
            return true;
        }else{
            return false;
        }
    }
}
