package com.seu.wufan.alumnicircle.api.entity;

import java.util.List;

/**
 * @author wufan
 * @date 2016/4/25
 */
public class PublishDynamicReq {

    private String news_text;
    private List<String> images;
    private String topic_id;

    public String getNews_text() {
        return news_text;
    }

    public void setNews_text(String news_text) {
        this.news_text = news_text;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }
}
