package com.seu.wufan.alumnicircle.qualifier;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        PreferenceType.PHONE,
        PreferenceType.ACCESS_TOKEN,
        PreferenceType.AVATAR
})
public @interface PreferenceType {
    String PHONE = "com.seu.wufan.alumnicircle.phone";
    String ACCESS_TOKEN = "com.seu.wufan.alumnicircle.access";
    String AVATAR = "com.seu.wufan.alumnicircle.avatar";
}
