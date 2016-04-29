package com.seu.wufan.alumnicircle.api.entity.item;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class TopicDynamicItem {

    /**
     * news_id :
     * post_time :
     * user_id :
     * text :
     * like_amount :
     * comment_amount :
     * share_amount :
     * news_type :
     */

    private String news_id;
    private String post_time;
    private String user_id;
    private String text;
    private String like_amount;
    private String comment_amount;
    private String share_amount;
    private String news_type;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
