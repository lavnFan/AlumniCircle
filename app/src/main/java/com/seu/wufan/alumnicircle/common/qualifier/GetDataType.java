package com.seu.wufan.alumnicircle.common.qualifier;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        GetDataType.FIRST_GET_DATA,
        GetDataType.LOAD_MORE
})

public @interface GetDataType{
    int FIRST_GET_DATA = 0;
    int LOAD_MORE = 1;
}