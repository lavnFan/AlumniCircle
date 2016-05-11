package com.umeng.common.ui.util;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.common.ui.listener.FrinendClickSpanListener;
import com.umeng.common.ui.listener.TopicClickSpanListener;

/**
 * Created by wangfei on 16/3/28.
 */
public class WebViewSettingUtil {
    public static void initSetting(WebSettings webSettings){
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//            settings.setAppCacheEnabled(true);
//            settings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
    }
    public static void SetWebiew(WebView view,int style,FeedItem mFeedItem,TopicClickSpanListener topicClickSpanListener,FrinendClickSpanListener friendClickSpanListener,Context context){
        WebSettings settings = view.getSettings();
      initSetting(settings);
        view.setWebViewClient(new WebClient(style,topicClickSpanListener,friendClickSpanListener,context));
        String content = "<html>"+mFeedItem.rich_content+"</html>";

        view.loadData(content, "text/html;charset=UTF-8", null);
    }
}
