package com.seu.wufan.alumnicircle.ui.activity.circle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.utils.ToastUtils;
import com.seu.wufan.alumnicircle.mvp.presenter.circle.PublishDynamicIPresenter;
import com.seu.wufan.alumnicircle.mvp.views.activity.IPublishDynamicView;
import com.seu.wufan.alumnicircle.ui.activity.MainActivity;
import com.seu.wufan.alumnicircle.ui.adapter.circle.PhotoAdapter;
import com.seu.wufan.alumnicircle.ui.dialog.ProgressDialog;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * @author wufan
 * @date 2016/2/24
 */
public class PublishDynamicActivity extends BaseSwipeActivity implements IPublishDynamicView {

    private final int PICK_PHOTO_REQUEST_CODE = 1;

    @Bind(R.id.circle_publish_dynamic_edit_text)
    EditText mDynamicEditText;
    @Bind(R.id.circle_publish_dynamic_iv)
    ImageView mDynamicIv;
    @Bind(R.id.circle_publish_dynamic_image_recycle_view)
    RecyclerView imageRecyclerView;

    ArrayList<String> photoPaths = new ArrayList<>();
    PhotoAdapter photoAdapter;

    @Inject
    PublishDynamicIPresenter presenter;

    ProgressDialog progressDialog;

    @Override
    protected int getContentView() {
        return R.layout.activity_circle_publish_dynamic;
    }

    @Override
    protected void prepareDatas() {
        getApiComponent().inject(this);
        presenter.attachView(this);
    }

    @Override
    protected void initViewsAndEvents() {
        setToolbarTitle("发动态");
        setToolbarRightTitle("发布");
        photoAdapter = new PhotoAdapter(this, photoPaths);
        imageRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        imageRecyclerView.setAdapter(photoAdapter);
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void publishSuccess() {
        if(progressDialog!=null){
            progressDialog.dismiss();
            showToast("发布成功");
        }
        readyGoThenKill(MainActivity.class);
    }

    @Override
    public void publishFailed() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetCantUse() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        ToastUtils.showNetCantUse(this);
    }

    @Override
    public void showNetError() {
        ToastUtils.showNetError(this);
    }

    @Override
    public void showToast(@NonNull String s) {
        ToastUtils.showToast(s,this);
    }

    @OnClick(R.id.circle_publish_dynamic_iv)
    void selectPhotos() {
        PhotoPickerIntent intent = new PhotoPickerIntent(this);
        intent.setPhotoCount(9);
        intent.setShowCamera(true);
        intent.setShowGif(true);
        startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Photo> photos;
        if (resultCode == RESULT_OK && requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (data != null) {
                photos = data.getParcelableArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                photoPaths.clear();
                for (Photo photo : photos) {
                    photoPaths.add(photo.getPath());
                    Log.i("TAG",photo.getPath());
                }
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.text_toolbar_right_tv)
    void publishDynamic(){
        if(!mDynamicEditText.getText().toString().isEmpty()){
            progressDialog = new ProgressDialog(this);
            progressDialog.setContent("正在发布");
            progressDialog.show();
            presenter.publishDynamic(photoPaths.size(),mDynamicEditText.getText().toString(),photoPaths,null);
        }

    }

}
