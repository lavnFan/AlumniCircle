package com.seu.wufan.alumnicircle.api.entity;

/**
 * @author wufan
 * @date 2016/5/24
 */
public class WeixinReq {
    private String access_token;
    private String open_id;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }
}
