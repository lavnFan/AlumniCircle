package com.seu.wufan.alumnicircle.mvp.model;

import android.support.annotation.Nullable;

import com.seu.wufan.alumnicircle.api.ContactsApi;
import com.seu.wufan.alumnicircle.common.provider.TokenProvider;

/**
 * @author wufan
 * @date 2016/4/24
 */
public class ContactsModel extends BaseModel<ContactsApi>{

    public ContactsModel(@Nullable TokenProvider tokenProvider) {
        super(tokenProvider);
    }

    @Override
    protected Class<ContactsApi> getServiceClass() {
        return ContactsApi.class;
    }
}
