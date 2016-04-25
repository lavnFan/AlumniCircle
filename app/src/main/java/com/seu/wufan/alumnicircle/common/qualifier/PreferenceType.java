package com.seu.wufan.alumnicircle.common.qualifier;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        PreferenceType.PHONE,
        PreferenceType.ACCESS_TOKEN,
        PreferenceType.USER_ID
})
public @interface PreferenceType {
    String PHONE = "com.icon_seu.wufan.alumnicircle.phone";
    String ACCESS_TOKEN = "com.icon_seu.wufan.alumnicircle.access";
    String USER_ID = "com.icon_seu.wufan.alumnicircle.user_id";
}
