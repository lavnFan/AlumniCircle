package com.seu.wufan.alumnicircle.common.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.seu.wufan.alumnicircle.R;

/**
 * @author wufan
 * @date 2016/2/29
 */
public class ToastUtils {

    public static void showToast(String s,Context context){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void showNetCantUse(Context context){
        Toast.makeText(context,context.getString(R.string.network_error_tips),Toast.LENGTH_SHORT).show();
    }

    public static void showNetError(Context context){
        Toast.makeText(context,context.getString(R.string.common_empty_msg),Toast.LENGTH_SHORT).show();
    }
}
