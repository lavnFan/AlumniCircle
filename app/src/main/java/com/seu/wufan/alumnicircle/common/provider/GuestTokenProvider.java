package com.seu.wufan.alumnicircle.common.provider;

public class GuestTokenProvider implements TokenProvider{
    @Override
    public String getToken() {
        return "guest";
    }
}
