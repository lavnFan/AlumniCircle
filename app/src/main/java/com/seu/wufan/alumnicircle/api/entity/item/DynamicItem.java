package com.seu.wufan.alumnicircle.api.entity.item;

import java.util.List;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class DynamicItem {


    /**
     * news_id :
     * post_time :
     * user_id :
     * user_image :
     * name :
     * enroll_year :
     * user_job :
     * user_major :
     * news_text :
     * like_amount :
     * comment_amount :
     * share_amount :
     * news_type :
     * topic_id :
     * share_news_id :
     * share_user_id :
     * share_news_text :
     */

    private String news_id;
    private String post_time;
    private String user_id;
    private String user_image;
    private String name;
    private String enroll_year;
    private String user_job;
    private String user_major;
    private String news_text;
    private String like_amount;
    private String comment_amount;
    private String share_amount;
    private String news_type;
    private String topic_id;
    private String share_news_id;
    private String share_user_id;
    private String share_news_text;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    private List<String> images;

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnroll_year() {
        return enroll_year;
    }

    public void setEnroll_year(String enroll_year) {
        this.enroll_year = enroll_year;
    }

    public String getUser_job() {
        return user_job;
    }

    public void setUser_job(String user_job) {
        this.user_job = user_job;
    }

    public String getUser_major() {
        return user_major;
    }

    public void setUser_major(String user_major) {
        this.user_major = user_major;
    }

    public String getNews_text() {
        return news_text;
    }

    public void setNews_text(String news_text) {
        this.news_text = news_text;
    }

    public String getLike_amount() {
        return like_amount;
    }

    public void setLike_amount(String like_amount) {
        this.like_amount = like_amount;
    }

    public String getComment_amount() {
        return comment_amount;
    }

    public void setComment_amount(String comment_amount) {
        this.comment_amount = comment_amount;
    }

    public String getShare_amount() {
        return share_amount;
    }

    public void setShare_amount(String share_amount) {
        this.share_amount = share_amount;
    }

    public String getNews_type() {
        return news_type;
    }

    public void setNews_type(String news_type) {
        this.news_type = news_type;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getShare_news_id() {
        return share_news_id;
    }

    public void setShare_news_id(String share_news_id) {
        this.share_news_id = share_news_id;
    }

    public String getShare_user_id() {
        return share_user_id;
    }

    public void setShare_user_id(String share_user_id) {
        this.share_user_id = share_user_id;
    }

    public String getShare_news_text() {
        return share_news_text;
    }

    public void setShare_news_text(String share_news_text) {
        this.share_news_text = share_news_text;
    }
}
