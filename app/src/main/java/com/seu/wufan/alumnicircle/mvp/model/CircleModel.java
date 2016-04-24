package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.CircleApi;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

/**
 * @author wufan
 * @date 2016/4/24
 */
public class CircleModel extends BaseModel<CircleApi>{

    public CircleModel(@Nullable TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    @Override
    protected Class<CircleApi> getServiceClass() {
        return CircleApi.class;
    }
}
