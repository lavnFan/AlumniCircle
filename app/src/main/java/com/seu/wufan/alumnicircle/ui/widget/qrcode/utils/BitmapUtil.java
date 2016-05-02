package com.seu.wufan.alumnicircle.ui.widget.qrcode.utils;

import android.graphics.BitmapFactory;

public class BitmapUtil {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        int width = options.outWidth;
        int height = options.outHeight;

        if (height > reqHeight || width > reqWidth) {
            if (width < height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

}
