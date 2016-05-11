package com.umeng.common.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;

/**
 * Created by umeng on 12/17/15.
 */
public class RoundCornerImageView extends SquareImageView {
    private float mCornerRaduis = 0f;
    private Paint mPaint;
    private RectF mRect;
    private int mWidth;
    private Matrix mMatrix;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context);
    }

    private void init(AttributeSet attributeSet,Context context){
//        TypedArray a = context.obtainStyledAttributes(attributeSet, ResFinder.getStyleableArrts("RoundCornerImageView"));
//        try{
            mMatrix = new Matrix();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            //mCornerRaduis = a.getDimensionPixelSize(ResFinder.getStyleableIndex("RoundCornerImageView_radius"), 0);
            mRect = new RectF();
//        }
//        finally {
//            a.recycle();
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mWidth = Math.min(width, height);
//        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if(getDrawable()==null){
            return;
        }
        setUpShader();
        mRect.set(0f, DeviceUtils.dp2px(getContext(), 1.5f), getWidth(), getHeight());
        canvas.drawRoundRect(mRect, mCornerRaduis, mCornerRaduis, mPaint);
    }

    private void setUpShader() {

        Drawable drawable = getDrawable();
        Bitmap bmp = drawableToBitamp(drawable);
        if (drawable == null || bmp == null) {
            return;
        }
        BitmapShader mBitmapShader;
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        // 拿到bitmap宽或高的小值
        int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
        scale = mWidth * 1.0f / bSize;

        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mPaint.setShader(mBitmapShader);
    }

    protected Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

//    private int measureWidth(int pWidthMeasureSpec) {
//        int result = 0;
//        int widthMode = View.MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
//        int widthSize = View.MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
//
//        switch (widthMode) {
//            /**
//             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
//             * MeasureSpec.AT_MOST。 MeasureSpec.EXACTLY是精确尺寸，
//             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
//             * :layout_width="50dp"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
//             * MeasureSpec.AT_MOST是最大尺寸，
//             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
//             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
//             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
//             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
//             * 通过measure方法传入的模式。
//             */
//            case View.MeasureSpec.AT_MOST:
//            case View.MeasureSpec.EXACTLY:
//                result = widthSize;
//                break;
//        }
//        return result;
//    }

//    private int measureHeight(int pHeightMeasureSpec) {
//        int result = 0;
//
//        int heightMode = View.MeasureSpec.getMode(pHeightMeasureSpec);
//        int heightSize = View.MeasureSpec.getSize(pHeightMeasureSpec);
//
//        switch (heightMode) {
//            case View.MeasureSpec.AT_MOST:
//            case View.MeasureSpec.EXACTLY:
//                result = heightSize;
//                break;
//        }
//        return result;
//    }

    public float getmCornerRaduis() {
        return mCornerRaduis;
    }

    public void setmCornerRaduis(float mCornerRaduis) {
        this.mCornerRaduis = mCornerRaduis;
        invalidate();
        requestLayout();
    }
}
