package com.seu.wufan.alumnicircle.model.api;

/**
 * @author wufan
 * @date 2016/2/22
 */
public class LoginRes {

    private String user_id;
    private String access_token;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
