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
package com.umeng.common.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ResFinder;


public class ChatInputView extends LinearLayout {

    private TextView mTextView;
    private EditText mEditTextView;
    private TextView mSendBtn;

    public ChatInputView(Context context) {
        super(context);
        init();
    }

    public ChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundResource(android.R.color.black);
        LayoutInflater.from(getContext()).inflate(ResFinder.getLayout("umeng_comm_chat_input_view"), this, true);
        mEditTextView = (EditText)findViewById(ResFinder.getId("umeng_comm_message_chat_edittext"));
        mSendBtn = (TextView)findViewById(ResFinder.getId("umeng_comm_message_chat_send_btn"));
        mSendBtn.setEnabled(false);
        initTextWatcher();
    }

    public void setCharDisplay(TextView textView){
        this.mTextView = textView;
    }

    private void initTextWatcher() {
        mEditTextView.setGravity(Gravity.LEFT & Gravity.TOP);
        mEditTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditTextView.setSingleLine(false);
        mEditTextView.setHorizontallyScrolling(false);
        mEditTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Editable editable = s;
                int totalChars = editable.toString().length();
                if (totalChars > Constants.COMMENT_CHARS) {
//                    mEditTextView.setText(editable.delete(Constants.COMMENT_CHARS, totalChars));
//                    mEditTextView.setSelection(mEditTextView.getText().length());
//                    String text = "评论最多" + Constants.COMMENT_CHARS + "个字符~";
//                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                    if (mTextView != null) {
                        mTextView.setText(totalChars + "/" + "300");
                        mTextView.setTextColor(Color.RED);
                    }
                } else {
                    if (mTextView != null) {
                        mTextView.setText(totalChars + "/" + "300");
                        mTextView.setTextColor(Color.BLACK);
                    }

                }
                if (totalChars > Constants.COMMENT_CHARS || totalChars <= 0) {
                    mSendBtn.setEnabled(false);
                } else {
                    mSendBtn.setEnabled(true);
                }
            }
        });
    }
}
