package com.seu.wufan.alumnicircle.common.provider;

import com.seu.wufan.alumnicircle.model.TokenModel;

/**
 * @author wufan
 * @date 2016/2/29
 */
public class UserTokenProvider implements TokenProvider{
    private String token;

    public UserTokenProvider(String token){
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}
