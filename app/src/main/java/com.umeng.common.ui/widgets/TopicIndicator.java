package com.umeng.common.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;

/**
 * Created by wangfei on 15/12/10.
 */
public class TopicIndicator extends MainIndicator{
    /**
     * 绘制三角形的画笔
     */
    protected Paint mPaint;

    protected Paint mLinePaint;
    /**
     * path构成一个三角形
     */
    private Path mPath;

    protected Paint mDividerLinePaint;

    /**
     * 三角形的宽度
     */
    private int mTriangleWidth;
    /**
     * 三角形的高度
     */
    private int mTriangleHeight;

    /**
     * 三角形的宽度为单个Tab的1/6
     */
    private static final float RADIO_TRIANGEL = 1.0f / 6;
    /**
     * 三角形的最大宽度
     */
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 6 * RADIO_TRIANGEL);

    /**
     * 初始时，三角形指示器的偏移量
     */
    private int mInitTranslationX;
    /**
     * 手指滑动时的偏移量
     */
    private float mTranslationX;

    /**
     * 默认的Tab数量
     */
    private static final int COUNT_DEFAULT_TAB = 4;
    /**
     * tab数量
     */
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;

    /**
     * tab上的内容
     */
    private String[] mTabTitles;
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;
    private int mHalfScreenWidth = 0;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = ResFinder
            .getColor("umeng_comm_text_default_color");
    /**
     * 标题选中时的颜色
     */
    protected static final int COLOR_TEXT_HIGHLIGHTCOLOR = ResFinder
            .getColor("umeng_comm_text_topic_light_color");

    public TopicIndicator(Context context) {
        this(context, null);
    }

    public TopicIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(3);

        mDividerLinePaint = new Paint();
        mDividerLinePaint.setAntiAlias(true);
        mDividerLinePaint.setColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        mDividerLinePaint.setStyle(Paint.Style.FILL);
        mDividerLinePaint.setStrokeWidth(5);
//        ColorDrawable dw = new ColorDrawable(11111111);
//        setDividerPadding(10);
//        this.setDividerDrawable(dw);
    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        // 画笔平移到正确的位置
        canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 5);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

//        // 绘制tab的下划线
//        canvas.drawLine(mTranslationX, getHeight()-18, mTranslationX
//                + mHalfScreenWidth, getHeight()-18, mLinePaint);
        // 绘制一行的分割线
//        canvas.drawLine(0, getHeight() - 1, 2 * mHalfScreenWidth, getHeight() - 1,
//                mDividerLinePaint);
        super.dispatchDraw(canvas);
    }

    /**
     * 初始化三角形的宽度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);// 1/6 of
        // width
        mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);

        // 初始化三角形
        initTriangle();

        // 初始时的偏移量
        mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth
                / 2;
    }

    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;
    }

    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */

    public void setTabItemTitles(String[] datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.length > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (int i = 0; i <mTabTitles.length;i++) {
                // 添加view
                addView(generateView(i,mTabTitles[i]));
            }

            // 设置item的click事件
            setItemClickEvent();
            mHalfScreenWidth = (((getScreenWidth()) / mTabVisibleCount)/10)*8;
        }

    }

    /**
     * 对外的ViewPager的回调接口
     *
     * @author zhy
     */
    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }
    /**
     *
     * 每个item的click事件
     * @author wf
     */
    public interface IndicatorListener {
        public void SetItemClick();
    }
    public void SetIndictorClick(IndicatorListener listener){
        listener.SetItemClick();
    }
    // 对外的ViewPager的回调接口
    private PageChangeListener onPageChangeListener;

    // 对外的ViewPager的回调接口的设置
    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    // 设置关联的ViewPager
    public void setViewPager(ViewPager mViewPager, int pos) {
        this.mViewPager = mViewPager;

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                highLightTextView(position);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }

            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(pos);
        // 高亮
        highLightTextView(pos);
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(ResFinder.getColor("umeng_comm_feed_detail_blue"));



        }

    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(ResFinder.getColor("umeng_comm_color_99"));

            }
        }
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */

    private TextView generateView(int index,String text) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
              0, CommonUtils.dip2px(getContext(),40));
        lp.weight = 1;

            tv.setGravity(Gravity.CENTER);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            tv.setBackground(ResFinder.getDrawable("umeng_comm_retangle"));
        }else{
            tv.setBackgroundResource(ResFinder.getResourceId(ResFinder.ResType.DRAWABLE,"umeng_comm_retangle"));
        }
        tv.setTextColor(ResFinder.getColor("umeng_comm_color_99"));
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        mPath = new Path();

//        mTriangleHeight = (int) (mTriangleWidth * Math.sqrt(3) / 4); // 等边三角形
//        mPath.moveTo(0, 0);
//        mPath.lineTo(mTriangleWidth, 0);
//        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
//        mPath.close();

    }

    /**
     * 指示器跟随手指滚动，以及容器滚动
     *
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        /**
         * <pre>
         *  0-1:position=0 ;1-0:postion=0;
         * </pre>
         */
        // 不断改变偏移量，invalidate
        mTranslationX = (getWidth()-20) / mTabVisibleCount * (position + offset)+( (((getScreenWidth()) / mTabVisibleCount)/20)*2);

        int tabWidth = getScreenWidth() / mTabVisibleCount;

        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (offset > 0 && position >= (mTabVisibleCount - 2)
                && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth
                        + (int) (tabWidth * offset), 0);
            } else {
                // 为count为1时 的特殊处理
                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }

        invalidate();
    }

    /**
     * 设置布局中view的一些必要属性；如果设置了setTabTitles，布局中view则无效
     */
    @Override
    protected void onFinishInflate() {
        Log.e("TAG", "onFinishInflate");
        super.onFinishInflate();

        int cCount = getChildCount();

        if (cCount == 0)
            return;

        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view
                    .getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        // 设置点击事件
        setItemClickEvent();

    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
