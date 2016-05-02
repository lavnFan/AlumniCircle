package com.seu.wufan.alumnicircle.ui.widget.qrcode.dwj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.BitmapUtil;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.QRGenerateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GenerateActivity extends AppCompatActivity {

    @Bind(R.id.et_text)
    EditText mEditText;
    @Bind(R.id.iv_barcode)
    ImageView mImageView;
    @Bind(R.id.center_pic)
    ImageView mCenterPic;
    @Bind(R.id.bt_save)
    Button mBtnSave;

    private Bitmap mBitmap;
    private Bitmap mQrcode;
    private String mDirPath;

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generate);
        ButterKnife.bind(this);
        setTitle("生成二维码");
        getApplication().getFilesDir();
        mDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QrCode";
//        mDirPath = getApplication().getFilesDir() + "/QrCode";
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mCenterPic.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.bt_select_pic)
    public void onSelectPic(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.center_pic)
    public void centerPic(View v) {
        if (mBitmap != null) {
            mBitmap = null;
            mCenterPic.setVisibility(View.INVISIBLE);
        }
    }

    /*public static File getFilePath(String filePath,
                                   String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }*/

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.bt_save)
    public void saveQrPic(View v) {
        if (mQrcode != null) {
            try {
                StringBuffer sb = new StringBuffer();
                sb.append(mDirPath).append("/qr").append(System.currentTimeMillis()).append(".jpg");
                String filePath = sb.toString();
                //makeRootDirectory(filePath);

                FileOutputStream outputStream = getApplicationContext().openFileOutput("text.txt", Context.MODE_PRIVATE);
                mQrcode.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(filePath)));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(new File(filePath));
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);

                mBtnSave.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "图片已保存到：" + mDirPath, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.bt_generate)
    public void onGenerate(View v) {
        final String s = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(getApplication(), "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQrcode = QRGenerateUtil.createQRImage(s, 600, 600, mBitmap);
                if (mQrcode != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(mQrcode);
                            mBtnSave.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                //获取图片轮廓
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                //计算采样率
                int reqWidth = 100;
                int reqHeight = 100;
                int sampleSize = BitmapUtil.calculateInSampleSize(options, reqWidth, reqHeight);
                //加载图片
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                mCenterPic.setImageBitmap(mBitmap);
                mCenterPic.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



}
