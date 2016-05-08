package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.CircleApi;
import com.seu.wufan.alumnicircle.api.entity.DynamicListRes;
import com.seu.wufan.alumnicircle.api.entity.DynamicRes;
import com.seu.wufan.alumnicircle.api.entity.PublishDynamicReq;
import com.seu.wufan.alumnicircle.api.entity.QnReq;
import com.seu.wufan.alumnicircle.api.entity.QnRes;
import com.seu.wufan.alumnicircle.api.entity.TopicDynamicRes;
import com.seu.wufan.alumnicircle.api.entity.TopicRes;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

import java.util.List;

import retrofit2.http.Body;
import rx.Observable;

/**
 * @author wufan
 * @date 2016/4/24
 */
public class CircleModel extends BaseModel<CircleApi>{

    public CircleModel(@Nullable TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    @Override
    protected Class<CircleApi> getServiceClass() {
        return CircleApi.class;
    }

    public Observable<Void> publishDynamic(String news_text, List<String> images, String topic_id){
        PublishDynamicReq req = new PublishDynamicReq();
        req.setImages(images);
        req.setNews_text(news_text);
        req.setTopic_id(topic_id);
        return getService().publishDynamic(req);
    }

    public Observable<List<DynamicItem>> getNewDynamic(String page){
        return  getService().getNewDynamic(page);
    }

    Observable<DynamicListRes> getUserDynamic(String page,String user_id){
        return getService().getUserDynamic(page,user_id);
    }

    public Observable<DynamicRes> getDynamic(String news_id){
       return getService().getDynamic(news_id);
    }

    public Observable<TopicRes> getTopic(){
        return getService().getTopic();
    }

    Observable<TopicDynamicRes> getTopicDynamic(String topic_id){
        return getService().getTopicDynamic(topic_id);
    }

    public Observable<List<QnRes>> createQiNiuToken(int count){
        QnReq req = new QnReq();
        req.setCount(count);
        return getService().createQiNiuToken(req);
    }
}
