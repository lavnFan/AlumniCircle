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

package com.seu.wufan.alumnicircle.ui.activity.custom;

import android.content.Context;
import android.content.Intent;

import com.seu.wufan.alumnicircle.mvp.presenter.login.LoginIPresenter;
import com.seu.wufan.alumnicircle.ui.activity.login.LoginActivity;
import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.ui.activities.FindActivity;

/**
 * 自定义实现登录系统,继承自AbsLoginImpl，只需要在onLogin中实现登录功能即可
 */
public class SimpleLoginImpl extends AbsLoginImpl {

    @Override
    protected void onLogin(Context context, final LoginListener listener) {
        // 包装一下Listener
//        LoginIPresenter.sLoginListener = listener;
        // 跳转到你的Activity
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

}
