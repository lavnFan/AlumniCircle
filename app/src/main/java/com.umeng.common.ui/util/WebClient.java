package com.umeng.common.ui.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicItemResponse;
import com.umeng.common.ui.activities.BrowserActivity;
import com.umeng.common.ui.listener.FrinendClickSpanListener;
import com.umeng.common.ui.listener.TopicClickSpanListener;

/**
 * Created by wangfei on 16/3/28.
 */
public class WebClient extends WebViewClient{
    private int style;
    Context context;
    TopicClickSpanListener topicClickSpanListener;
    FrinendClickSpanListener friendClickSpanListener;
    public WebClient(int style,TopicClickSpanListener topicClickSpanListener,FrinendClickSpanListener friendClickSpanListener,Context context){
        super();
        this.style =style;
        this.topicClickSpanListener = topicClickSpanListener;
        this.friendClickSpanListener = friendClickSpanListener;
        this.context = context;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (style == 0&& context!=null){
            if (url.contains("user_id")){
                CommUser user= new CommUser();
                user.id = url.replace("http:/?user_id=","");
                user.id = user.id.replace("/","");
                user.id = user.id.replace("&","");
                if(friendClickSpanListener!=null){
                    friendClickSpanListener.onClick(user);
                }
                return true;
            }
            if (url.contains("topic_id")){
                Topic topic = new Topic();
                topic.id = url.replace("http:?topic_id=","");
                topic.id = topic.id.replace("/","");
                topic.id = topic.id.replace("&","");

                CommunitySDKImpl.getInstance().fetchTopicWithId(topic.id, new Listeners.FetchListener<TopicItemResponse>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(TopicItemResponse response) {

                        if (topicClickSpanListener!=null&& !TextUtils.isEmpty(response.result.name)){
                            topicClickSpanListener.onClick(response.result);
                        }
                    }
                });

                return true;
            }
            if (url.startsWith("http")){
                Intent intent = new Intent(context,
                        BrowserActivity.class);
                intent.putExtra(BrowserActivity.URL, url);
                context.startActivity(intent);
            }
        }
        return true;
    }

    @Override
    public void onLoadResource(WebView view, String url) {

    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      String url) {



        return null;
    }
}
