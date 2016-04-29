package com.seu.wufan.alumnicircle.api.entity.item;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class DynamicCommentItem {

    /**
     * comment_id :
     * comment_text :
     * user_id :
     * reply_user_id :
     * post_time :
     */

    private String comment_id;
    private String comment_text;
    private String user_id;
    private String reply_user_id;
    private String post_time;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReply_user_id() {
        return reply_user_id;
    }

    public void setReply_user_id(String reply_user_id) {
        this.reply_user_id = reply_user_id;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }
}
