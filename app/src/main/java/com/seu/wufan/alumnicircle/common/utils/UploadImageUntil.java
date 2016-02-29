package com.seu.wufan.alumnicircle.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import java.io.ByteArrayOutputStream;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UploadImageUntil {
    private UploadManager uploadManager;

    public UploadImageUntil(){
        uploadManager = new UploadManager();
    }

    public void upLoadImage(final String path, final String key, final String token,
                            final UpCompletionHandler completionHandler, final UploadOptions options){
        Observable.create(new Observable.OnSubscribe<ByteArrayOutputStream>() {
            @Override
            public void call(Subscriber<? super ByteArrayOutputStream> subscriber) {
                ByteArrayOutputStream baos = compressImage(path);
                subscriber.onStart();
                subscriber.onNext(baos);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ByteArrayOutputStream>() {
            @Override
            public void call(ByteArrayOutputStream byteArrayOutputStream) {
                byte[] data = byteArrayOutputStream.toByteArray();
                uploadManager.put(data, key, token, completionHandler, options);
            }
        });
    }

    private ByteArrayOutputStream compressImage(String imagePath){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //设置为true获取图片的初始大小
        opts.inJustDecodeBounds = true;
        Bitmap image = BitmapFactory.decodeFile(imagePath,opts);
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        opts.inJustDecodeBounds = false;

        //控制图片高宽中较低的一个在500像素左右
        if(Math.min(imageHeight,imageWidth) > 500){
            float ratio = Math.max(imageHeight,imageWidth)/500;
            opts.inSampleSize = Math.round(ratio);
        }
        Bitmap finalImage = BitmapFactory.decodeFile(imagePath,opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //降低画质
        finalImage.compress(Bitmap.CompressFormat.JPEG,70,baos);
        return baos;
    }
}
