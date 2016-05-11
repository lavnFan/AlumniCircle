
package com.umeng.common.ui.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.model.AlbumModel;
import com.umeng.common.ui.model.PhotoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activeandroid.util.IOUtils;


/**
 * 相册Controller,用于获取相册、图片列表
 */
public class AlbumController {

    private ContentResolver resolver;

    public AlbumController(Context context) {
        resolver = context.getContentResolver();
    }

    /**
     * 获取最近使用的照片
     * 
     * @return
     */
    public List<PhotoModel> getCurrent() {
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] {
                ImageColumns.DATA,
                ImageColumns.DATE_ADDED, ImageColumns.SIZE
        }, null, null, ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<PhotoModel>();
        List<PhotoModel> photos = new ArrayList<PhotoModel>();
        cursor.moveToLast();
        do {
//            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
                PhotoModel photoModel = new PhotoModel();
                photoModel.setOriginalPath(cursor.getString(cursor
                        .getColumnIndex(ImageColumns.DATA)));
                photos.add(photoModel);
//            }
        } while (cursor.moveToPrevious());
        IOUtils.closeQuietly(cursor);
        return photos;
    }

    /**
     * 获取所有相册
     * 
     * @return
     */
    public List<AlbumModel> getAlbums() {
        List<AlbumModel> albums = new ArrayList<AlbumModel>();
        Map<String, AlbumModel> map = new HashMap<String, AlbumModel>();
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] {
                ImageColumns.DATA,
                ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE
        }, null, null, null);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<AlbumModel>();
        cursor.moveToLast();
        AlbumModel current = new AlbumModel(ResFinder.getString("umeng_comm_recent_photos"), 0,
                cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // 最近的照片
        albums.add(current);
        do {
//            if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10) {
//                continue;
//            }

            current.increaseCount();
            String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
            if (map.keySet().contains(name))
                map.get(name).increaseCount();
            else {
                AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor
                        .getColumnIndex(ImageColumns.DATA)));
                map.put(name, album);
                albums.add(album);
            }
        } while (cursor.moveToPrevious());
        IOUtils.closeQuietly(cursor);
        return albums;
    }

    /**
     * 获取某个相册中的所有图片
     * 
     * @param name
     * @return
     */
    public List<PhotoModel> getAlbum(String name) {
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] {
                ImageColumns.BUCKET_DISPLAY_NAME,
                ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE
        }, "bucket_display_name = ?",
                new String[] {
                    name
                }, ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<PhotoModel>();
        List<PhotoModel> photos = new ArrayList<PhotoModel>();
        cursor.moveToLast();
        do {
//            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
                PhotoModel photoModel = new PhotoModel();
                photoModel.setOriginalPath(cursor.getString(cursor
                        .getColumnIndex(ImageColumns.DATA)));
                photos.add(photoModel);
//            }
        } while (cursor.moveToPrevious());
        IOUtils.closeQuietly(cursor);
        return photos;
    }
}
