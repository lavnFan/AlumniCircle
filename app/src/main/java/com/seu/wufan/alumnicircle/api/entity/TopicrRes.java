package com.seu.wufan.alumnicircle.api.entity;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class TopicrRes {

    /**
     * topic_id :
     * title :
     * topic_text :
     * topic_date :
     * image :
     */

    private String topic_id;
    private String title;
    private String topic_text;
    private String topic_date;
    private String image;

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic_text() {
        return topic_text;
    }

    public void setTopic_text(String topic_text) {
        this.topic_text = topic_text;
    }

    public String getTopic_date() {
        return topic_date;
    }

    public void setTopic_date(String topic_date) {
        this.topic_date = topic_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
