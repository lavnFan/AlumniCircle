package com.seu.wufan.alumnicircle.ui.adapter.circle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.seu.wufan.alumnicircle.common.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.R;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private ArrayList<String> photoPaths ;
    private LayoutInflater inflater;

    private Context mContext;

    public PhotoAdapter(Context mContext) {
        this.photoPaths = new ArrayList<String>();
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }
    public PhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        //     CommonUtils.showImageWithGlide(mContext,holder.ivPhoto,photoPaths.get(position));
        Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

        Log.i("TAG", uri.toString());
        Glide.with(mContext)
                .load(uri)
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_photo_black_48dp)
                .error(R.drawable.ic_broken_image_black_48dp)
                .into(holder.ivPhoto);

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoPagerActivity.class);
                intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, photoPaths);
                intent.putExtra(PhotoPagerActivity.EXTRA_SHOW_DELETE, true);
//        if (mContext instanceof MainActivity) {
//          ((MainActivity) mContext).previewPhoto(intent);
//        }
            }
        });

    }

    @Override
    public int getItemCount() {
        return photoPaths.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
            vSelected.setVisibility(View.GONE);
        }
    }

}
