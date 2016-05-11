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
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.emoji.EmojiHandler;


/**
 * 主要实现对back事件的监听。在评论的时候，点击返回键，需要同时隐藏软键盘并且让EditTextk控件消失。
 */
public class CommentEditText extends EditText {

    private EditTextBackEventListener mListener;
    private OnTextChangeListener mTextChangeListener;
    private TextView textView;

    public CommentEditText(Context context) {
        super(context);
        initTextWatcher();
    }

    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextWatcher();
    }

    public CommentEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTextWatcher();
    }
    public void setCharDisplay(TextView textView){
        this.textView = textView;
    }

    public void setTextChangeListener(OnTextChangeListener l){
        this.mTextChangeListener = l;
    }

    protected void initTextWatcher() {
        this.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Editable editable = getText();
                int totalChars = editable.toString().length();
                if (totalChars > CommConfig.getConfig().mCommentLen) {
//                    setText(editable.delete(Constants.COMMENT_CHARS, totalChars));
//                    setSelection(getText().length());
                    String text = "评论最多" + CommConfig.getConfig().mCommentLen + "个字符~";
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                    if (text!=null&&textView!=null) {
                        textView.setText(totalChars + "/" + CommConfig.getConfig().mCommentLen);
                        textView.setTextColor(Color.RED);
                    }
                } else {
                    if (textView!=null){

                        textView.setText(totalChars+"/" + CommConfig.getConfig().mCommentLen);
                        textView.setTextColor(ResFinder.getColor("umeng_comm_color_a5"));
                    }

                }
                if(mTextChangeListener != null){
                    mTextChangeListener.onTextChange(editable.toString());
                }
            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        // super.onTextChanged(text, start, lengthBefore, lengthAfter);
        EmojiHandler.addEmojis(getContext(), getText(), 65, 65, false);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP
                && mListener != null) {
            if (mListener.onClickBack()) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setEditTextBackListener(EditTextBackEventListener listener) {
        this.mListener = listener;
    }

    public interface EditTextBackEventListener {
        public boolean onClickBack();
    }

    public interface OnTextChangeListener{
        public void onTextChange(String str);
    }

}
