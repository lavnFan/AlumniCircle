package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.seu.wufan.alumnicircle.R;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;

/**
 * @author wufan
 * @date 2016/4/30
 */
public class ImageAdapter extends BaseAdapter{
    private HashMap<String, DrawableRequestBuilder<String>> imageBuff = new HashMap<>(); //用于gridview的图片缓存
    private ArrayDeque<String> imageKeysQueue = new ArrayDeque<>();
    private final int MAX_BUFF_SIZE = 5;
    private Context context;
    private List<String> images;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public ImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    public void setmEntities(List<String> mEntities) {
        this.images = mEntities;
    }

    public void addEntities(List<String> mEntities){
        this.images.addAll(mEntities);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (null == convertView) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        DisplayMetrics dm = imageView.getResources().getDisplayMetrics();
        int screenWidthDip = dm.widthPixels;
        int width;
        int height;
        switch (images.size()) {
            case 1:
                width = height = (int) (screenWidthDip * 0.9f);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                break;
            default:
                width = height = (int) (screenWidthDip * 0.9f) / 3;
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (imageBuff.containsKey(images.get(pos))) {
            imageBuff.get(images.get(pos)).crossFade().override(width, height).into(imageView);
        } else {
            DrawableRequestBuilder<String> drb = Glide.with(context).load(images.get(pos)).placeholder(R.drawable.placeholder);
            drb.crossFade().override(width, height).into(imageView);
            if (imageKeysQueue.size() >= MAX_BUFF_SIZE) {
                imageBuff.remove(imageKeysQueue.pop());
            }
            imageKeysQueue.push(images.get(pos));
            imageBuff.put(images.get(pos), drb);
        }
        return imageView;
    }
}
