
package com.umeng.common.ui.imagepicker;

import android.os.Bundle;

import com.umeng.common.ui.domain.PhotoSelectorDomain;
import com.umeng.common.ui.model.PhotoConstants;
import com.umeng.common.ui.model.PhotoModel;
import com.umeng.common.ui.util.ImagePickerUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 照片预览Activity
 */
public class PhotoPreviewActivity extends BasePhotoPreviewActivity implements
        PhotoSelectorActivity.OnLocalReccentListener {

    private PhotoSelectorDomain photoSelectorDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        init(getIntent().getExtras());
    }

    @SuppressWarnings("unchecked")
    protected void init(Bundle extras) {
        if (extras == null)
            return;

        if (extras.containsKey(PhotoConstants.PHOTO_PRVIEW_PHOTO)) { // 预览图片

//            photos = (List<PhotoModel>) extras.getSerializable(PhotoConstants.PHOTO_PRVIEW_PHOTO);
            selectedphotos = (ArrayList<PhotoModel>) extras.getSerializable(PhotoConstants.PHOTO_PRVIEW_PHOTO_COPY);

             photos = new ArrayList<PhotoModel>();
            for (PhotoModel temp:selectedphotos){
                photos.add(temp);
            }
            current = extras.getInt(PhotoConstants.PHOTO_POSITION, 0);
            for (PhotoModel temp:photos){
                photosUris.add(temp.getOriginalPath());
            }

            MAXNUM = extras.getInt("maxnum");
            updatePercent();
            bindData();
        } else if (extras.containsKey(PhotoConstants.PHOTO_ALBUM)) { // 点击图片查看
            selectedphotos = (ArrayList<PhotoModel>) extras.getSerializable("selected");
            MAXNUM = extras.getInt("maxnum");
            for (PhotoModel temp:selectedphotos){
                photosUris.add(temp.getOriginalPath());
            }
            String albumName = extras.getString(PhotoConstants.PHOTO_ALBUM); // 相册
            this.current = extras.getInt(PhotoConstants.PHOTO_POSITION)-1;
            if (!ImagePickerUtils.isNull(albumName)
                    && albumName.equals(PhotoSelectorActivity.RECCENT_PHOTO)) {
                photoSelectorDomain.getReccent(this);
            } else {
                photoSelectorDomain.getAlbum(albumName, this);
            }
        }
    }

    @Override
    public void onPhotoLoaded(List<PhotoModel> photos) {
        this.photos = photos;
        updatePercent();
        bindData(); // 更新界面
    }

}
