package com.seu.wufan.alumnicircle.api.entity.item;

/**
 * @author wufan
 * @date 2016/5/24
 */
public class TokenRes {
    String access_token;
    String user_id;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
