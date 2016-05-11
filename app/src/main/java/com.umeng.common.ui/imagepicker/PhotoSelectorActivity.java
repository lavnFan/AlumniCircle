
package com.umeng.common.ui.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.adapters.AlbumAdapter;
import com.umeng.common.ui.adapters.PhotoAdapter;
import com.umeng.common.ui.domain.PhotoSelectorDomain;
import com.umeng.common.ui.model.AlbumModel;
import com.umeng.common.ui.model.PhotoConstants;
import com.umeng.common.ui.model.PhotoModel;
import com.umeng.common.ui.presenter.impl.TakePhotoPresenter;
import com.umeng.common.ui.util.AnimationUtil;
import com.umeng.common.ui.util.ImagePickerUtils;
import com.umeng.common.ui.widgets.PhotoItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择Activity
 */
public class PhotoSelectorActivity extends Activity implements
        PhotoItemViewHolder.onItemClickListener, PhotoItemViewHolder.onPhotoItemCheckedListener, OnItemClickListener,
        OnClickListener {

    public static final int SINGLE_IMAGE = 1;
    public static final String KEY_MAX = "key_max";
    public static final String SELECTED = "selected";
    public static final String ADD_PHOTO_PATH = "add_image_path_sample";
    private int MAX_IMAGE;

    public static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_CAMERA = 1;

    public static String RECCENT_PHOTO = null;

    private GridView mPhotosGridView;
    private ListView mAblumListView;
    private Button btnOk;
    private TextView tvAlbum, tvPreview;
    private PhotoSelectorDomain photoSelectorDomain;
    private PhotoAdapter mPhotoAdapter;
    private AlbumAdapter albumAdapter;
    private RelativeLayout layoutAlbum;
    private View bgBtn;
    private ArrayList<PhotoModel> mSelectedPhotos;
    private TextView tvNumber;
    private TakePhotoPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RECCENT_PHOTO = ResFinder.getString("umeng_comm_recent_photos");
        requestWindowFeature(Window.FEATURE_NO_TITLE);//
        setContentView(ResFinder.getLayout("umeng_comm_imagepicker_photoselector"));
        mPresenter = new TakePhotoPresenter();
        parseIntentExtra(getIntent());
        initWidgets();
        initPhotoGridView();
        initAlbumListView();

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());
        photoSelectorDomain.getReccent(reccentListener);
        photoSelectorDomain.updateAlbum(albumListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    private void initWidgets() {
        // tvTitle = (TextView)
        // findViewById(ResFinder.getId("umeng_comm_tv_title_lh"));
        mPhotosGridView = (GridView) findViewById(ResFinder.getId("umeng_comm_gv_photos_ar"));
        mAblumListView = (ListView) findViewById(ResFinder.getId("umeng_comm_lv_ablum_ar"));
        btnOk = (Button) findViewById(ResFinder.getId("umeng_comm_btn_right_lh"));
        tvAlbum = (TextView) findViewById(ResFinder.getId("umeng_comm_tv_album_ar"));
        bgBtn = findViewById(ResFinder.getId("umeng_bg_button"));
        tvPreview = (TextView) findViewById(ResFinder.getId("umeng_comm_tv_preview_ar"));
        layoutAlbum = (RelativeLayout) findViewById(ResFinder.getId("umeng_comm_layout_album_ar"));
        tvNumber = (TextView) findViewById(ResFinder.getId("umeng_comm_tv_number"));
        updateSelectePhotoSize();
        updateOkButton();
        btnOk.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvPreview.setOnClickListener(this);
        bgBtn.setOnClickListener(this);

        findViewById(ResFinder.getId("umeng_comm_bv_back_lh")).setOnClickListener(this); // 返回按钮
    }

    private void initPhotoGridView() {
        mPhotoAdapter = new PhotoAdapter(getApplicationContext(),
                new ArrayList<PhotoModel>(), ImagePickerUtils.getWidthPixels(this),
                this, this);
        mPhotosGridView.setAdapter(mPhotoAdapter);
    }

    private void initAlbumListView() {
        albumAdapter = new AlbumAdapter(getApplicationContext(),
                new ArrayList<AlbumModel>());
        mAblumListView.setAdapter(albumAdapter);
        mAblumListView.setOnItemClickListener(this);
    }

    private void parseIntentExtra(Intent intent) {
        if (getIntent().getExtras() != null) {
            MAX_IMAGE = getIntent().getIntExtra(KEY_MAX, 9);
            // 初始化mSelectedPhotos
            mSelectedPhotos = new ArrayList<PhotoModel>();
            // 获取从外部传递进来的已选列表
            initSelectedPhotoModels(intent.getStringArrayListExtra(Constants.PICKED_IMAGES));
        } else {
            // 选中的图片
            mSelectedPhotos = new ArrayList<PhotoModel>();
        }

    }

    private void initSelectedPhotoModels(List<String> selectedList) {
        if (selectedList != null) {
            for (String path : selectedList) {
                if (!path.equals(ADD_PHOTO_PATH)) {
                    mSelectedPhotos.add(new PhotoModel(path, true));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ResFinder.getId("umeng_comm_btn_right_lh"))
            pickedImageDone();
        else if (v.getId() == ResFinder.getId("umeng_comm_tv_album_ar"))
            album();
        else if (v.getId() == ResFinder.getId("umeng_comm_tv_preview_ar"))
            preview();
        else if (v.getId() == ResFinder.getId("umeng_comm_bv_back_lh"))
            finish();
        else if (v.getId() == ResFinder.getId("umeng_bg_button")){
            if (layoutAlbum.getVisibility() == View.VISIBLE){
                hideAlbum();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            PhotoModel photoModel = new PhotoModel(ImagePickerUtils.query(
                    getApplicationContext(), data.getData()));
            // ///////////////////////////////////////////////////////////////////////////////////////////
            if (mSelectedPhotos.size() >= MAX_IMAGE) {
                String toastMsg = String.format(
                        ResFinder.getString("umeng_comm_max_img_limit_reached"),
                        MAX_IMAGE);
                Toast.makeText(
                        this, toastMsg, Toast.LENGTH_SHORT).show();
                photoModel.setChecked(false);
                mPhotoAdapter.notifyDataSetChanged();
            } else {
                if (!mSelectedPhotos.contains(photoModel)) {
                    mSelectedPhotos.add(photoModel);
                }
            }
            pickedImageDone();
        }
        if (requestCode == PhotoConstants.PHOTOVIEW){
            Bundle  bundle=  data.getBundleExtra("bundle");
            mSelectedPhotos = (ArrayList<PhotoModel>) bundle.getSerializable("selected");
            mPhotoAdapter.updateCheck(mSelectedPhotos);
            updateOkButton();
        }
        if (requestCode == PhotoConstants.PHOTOPREVIEW){
            Bundle  bundle=  data.getBundleExtra("bundle");
            mSelectedPhotos = (ArrayList<PhotoModel>) bundle.getSerializable("selected");
            mPhotoAdapter.updateCheck(mSelectedPhotos);
            updateOkButton();
            if (resultCode == 1){
                pickedImageDone();
            }
        }
        onTakedPhoto(requestCode);
    }
    private void updateOkButton(){
        if (mSelectedPhotos.size() == 0){
            btnOk.setSelected(false);
            btnOk.setText(ResFinder.getString("umeng_comm_next"));
            btnOk.setTextColor(ResFinder.getColor("umeng_comm_color_99"));
        }else {
            btnOk.setSelected(true);
            btnOk.setText(ResFinder.getString("umeng_comm_next") + "(" + mSelectedPhotos.size() + "/" + MAX_IMAGE + ")");
            btnOk.setTextColor(ResFinder.getColor("umeng_comm_white_color"));
        }

    }
    private void pickedImageDone() {
        if (mSelectedPhotos.isEmpty()) {
            setResult(RESULT_CANCELED);
        } else {
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.FEED_IMAGES, mSelectedPhotos);
            data.putExtras(bundle);
            setResult(RESULT_OK, data);
        }
        finish();
    }

    private void preview() {
        if(mSelectedPhotos.isEmpty()){
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhotoConstants.PHOTO_PRVIEW_PHOTO, mSelectedPhotos);

        bundle.putInt("maxnum", MAX_IMAGE);
//        ImagePickerUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
        ImagePickerUtils.launchActivityForResult(this, PhotoPreviewActivity.class, bundle, PhotoConstants.PHOTOPREVIEW);

    }

    private void album() {
        if (layoutAlbum.getVisibility() == View.GONE) {
            popAlbum();
        } else {
            hideAlbum();
        }
    }

    private void popAlbum() {
        layoutAlbum.setVisibility(View.VISIBLE);
        new AnimationUtil(getApplicationContext(), ResFinder.getResourceId(ResType.ANIM,
                "umeng_comm_translate_up_current"))
                .setLinearInterpolator().startAnimation(layoutAlbum);
    }

    private void hideAlbum() {
        new AnimationUtil(getApplicationContext(), ResFinder.getResourceId(ResType.ANIM,
                "umeng_comm_translate_down"))
                .setLinearInterpolator().startAnimation(layoutAlbum);
        layoutAlbum.setVisibility(View.GONE);
    }

    /** 点击查看照片 */
    @Override
    public void onItemClick(int position) {
        if (position == 0){
            if (mSelectedPhotos.size() >= MAX_IMAGE) {
                if(MAX_IMAGE == 9){
                    ToastMsg.showShortMsgByResName("umeng_comm_image_overflow");
                }else{
                    ToastMsg.showShortMsgByResName("umeng_comm_image_overflow_one");
                }
            }else {
                mPresenter.takePhoto();
            }
            return;
        }
        Bundle bundle = new Bundle();
        // if (tvAlbum.getText().toString().equals(RECCENT_PHOTO)) {
        // bundle.putInt("position", position - 1);
        // }
        // else {
        // bundle.putInt("position", position);
        // }
        bundle.putInt("position", position);

        bundle.putString("album", tvAlbum.getText().toString());
        bundle.putInt("maxnum", MAX_IMAGE);
        bundle.putSerializable("selected", mSelectedPhotos);
        ImagePickerUtils.launchActivityForResult(this, PhotoPreviewActivity.class, bundle, PhotoConstants.PHOTOVIEW);
    }

    @Override
    public void onCheckedChanged(PhotoItemViewHolder photoItem, PhotoModel photoModel,
            boolean isChecked) {
        // 不能超过最大数量
        if (mSelectedPhotos.size() >= MAX_IMAGE && isChecked) {
            if(MAX_IMAGE == 9){
                ToastMsg.showShortMsgByResName("umeng_comm_image_overflow");
            }else{
                ToastMsg.showShortMsgByResName("umeng_comm_image_overflow_one");
            }
            photoItem.updatePhotoItemState(false);
            return;
        }

        if (isChecked) {
            if (!mSelectedPhotos.contains(photoModel)) {
                mSelectedPhotos.add(photoModel);
            }
            tvPreview.setEnabled(true);
        } else {
            mSelectedPhotos.remove(photoModel);
        }
        updateOkButton();
        // 更新选中状态
        photoItem.updatePhotoItemState(isChecked);

        updateSelectePhotoSize();

        if (mSelectedPhotos.isEmpty()) {
            tvPreview.setEnabled(false);
            tvPreview.setText(ResFinder.getString("umeng_comm_preview"));
        }
    }

    private void updateSelectePhotoSize() {
        tvNumber.setText("(" + mSelectedPhotos.size() + ")");
    }

    @Override
    public void onBackPressed() {
        if (layoutAlbum.getVisibility() == View.VISIBLE) {
            hideAlbum();
        } else
            super.onBackPressed();
    }

    /** 相册列表点击事件 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
        for (int i = 0; i < parent.getCount(); i++) {
            AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
            if (i == position) {
                album.setCheck(true);
            }
            else {
                album.setCheck(false);
            }
        }
        albumAdapter.notifyDataSetChanged();
        hideAlbum();
        tvAlbum.setText(current.getName());
        if (current.getName().equals(RECCENT_PHOTO)) {
            photoSelectorDomain.getReccent(reccentListener);
        } else {
            photoSelectorDomain.getAlbum(current.getName(), reccentListener); // 获取选中相册的照片
        }
    }

    private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
        @Override
        public void onAlbumLoaded(List<AlbumModel> albums) {
            albumAdapter.update(albums);
        }
    };

    private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
        @Override
        public void onPhotoLoaded(List<PhotoModel> photos) {
            for (PhotoModel model : photos) {
                if (mSelectedPhotos.contains(model)) {
                    model.setChecked(true);
                }
            }
            PhotoModel model = new PhotoModel("camera");
            mPhotoAdapter.update(photos);
            mPhotoAdapter.insert(model,0);
            mPhotosGridView.setSelection(0); // 修改图片选择目录之后滚动到顶部
        }
    };

    /** 获取本地图库照片回调 */
    public static interface OnLocalReccentListener {
        public void onPhotoLoaded(List<PhotoModel> photos);
    }

    /** 获取本地相册信息回调 */
    public static interface OnLocalAlbumListener {
        public void onAlbumLoaded(List<AlbumModel> albums);
    }
    protected void onTakedPhoto(int requestCode) {

        if (requestCode != TakePhotoPresenter.REQUEST_IMAGE_CAPTURE) {
            return;
        }

        String imgUri = mPresenter.updateImageToMediaLibrary();
        PhotoModel model = new PhotoModel(imgUri);
        mSelectedPhotos.add(0,model);
        mPhotoAdapter.insert(model, 1);
        mPhotoAdapter.updateCheck(mSelectedPhotos);
        updateOkButton();
//        List<String> selectedList = mImageSelectedAdapter.getDataSource();
//        // 更新媒体库
//        selectedList.remove(Constants.ADD_IMAGE_PATH_SAMPLE);
//        if (selectedList.size() < 9) {
//            selectedList.add(imgUri);
//            appendAddImageIfLessThanNine(selectedList);
//        } else {
//            ToastMsg.showShortMsgByResName("umeng_comm_image_overflow");
//        }
//        mImageSelectedAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attach(this);
        ImageLoaderManager.getInstance().getCurrentSDK().reset();
        ImageLoaderManager.getInstance().getCurrentSDK().resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        ImageLoaderManager.getInstance().getCurrentSDK().reset();
    }
}
