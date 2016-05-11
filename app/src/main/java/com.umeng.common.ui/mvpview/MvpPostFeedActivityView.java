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
package com.umeng.common.ui.mvpview;

import android.location.Location;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.LocationItem;

import java.util.List;

/**
 * 发布Feed 的View接口
 */
public interface MvpPostFeedActivityView {
    /**
     * 发布feed
     */
    public void startPostFeed();

    /**
     * 发送成功之后清空编辑器等状态
     */
    public void clearState();

    /**
     * 重新发送时恢复feed数据
     */
    public void restoreFeedItem(FeedItem feedItem);


    public void changeLocLayoutState(Location location, List<LocationItem> locationItems);

    /**
     * 由于网络、内容等原因不能发送该Feed的回调
     */
    public void canNotPostFeed();


    /**
     * 发送完成
     */
    public void postFeedComplete(boolean result);
}
