package com.seu.wufan.alumnicircle.ui.widget.qrcode.dwj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.decode.DecodeUtils;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.BitmapUtil;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.DensityUtil;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.utils.TextUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DecodeActivity extends AppCompatActivity {

    @Bind(R.id.tv_result)
    TextView mTvResult;
    @Bind(R.id.image_qrcode)
    ImageView mImageQrcode;
    @Bind(R.id.btn_select_pic)
    Button mBtnSelectPic;

    private String mResult;
    private Bitmap mBitmap;
    private boolean mBtnMoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcde_decode);
        ButterKnife.bind(this);
        setTitle("图片解码");
        mImageQrcode.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mImageQrcode.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Uri data = getIntent().getData();
                if (data != null) {
                    decodeImage(data);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @OnClick(R.id.btn_select_pic)
    public void onSelectPic(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.tv_result)
    public void onResultClick(View v) {
        if (!TextUtil.isEmpty(mResult) && TextUtil.isUrl(mResult)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mResult));
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            decodeImage(uri);
        }
    }

    private void decodeImage(Uri uri) {
        try {
            if (!mBtnMoved) moveBtn();
            //获取图片轮廓
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            //计算采样率
            int reqWidth = mImageQrcode.getWidth();
            int reqHeight = mImageQrcode.getHeight();
            int sampleSize = BitmapUtil.calculateInSampleSize(options, reqWidth, reqHeight);
            //加载图片
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
            //显示图片
            mImageQrcode.setImageBitmap(mBitmap);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //解码Bitmap
                    mResult = new DecodeUtils(DecodeUtils.DECODE_DATA_MODE_ALL).decodeWithZxing(mBitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleResult(mResult);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveBtn() {
        long start = System.currentTimeMillis();
        mBtnMoved = true;
        int[] targetLocation = new int[2];
        mImageQrcode.getLocationInWindow(targetLocation);
        targetLocation[0] += mImageQrcode.getWidth() - mBtnSelectPic.getWidth();
        targetLocation[1] -= mBtnSelectPic.getHeight() + DensityUtil.dip2px(this, 3);

        int[] startLocation = new int[2];
        mBtnSelectPic.getLocationInWindow(startLocation);
        int x = targetLocation[0] - startLocation[0];
        int y = targetLocation[1] - startLocation[1];

        mBtnSelectPic.setTranslationX(x);
        mBtnSelectPic.setTranslationY(y);

        long end = System.currentTimeMillis();
        Log.d("jumtop", "useTime: " + (end - start) + "ms");
    }

    private void handleResult(String result) {
        if (!TextUtil.isEmpty(result)) {
            if (TextUtil.isUrl(result)) {
                mTvResult.setText(Html.fromHtml("<u>" + result + "</u>"));
            } else {
                mTvResult.setText(result);
            }
        } else {
            mTvResult.setText("");
            Toast.makeText(getApplicationContext(), "图片解析失败", Toast.LENGTH_SHORT).show();
        }
    }

}
