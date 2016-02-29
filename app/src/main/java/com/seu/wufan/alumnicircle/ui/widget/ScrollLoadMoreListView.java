package com.seu.wufan.alumnicircle.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author wufan
 * @date 2016/2/4
 */
public class ScrollLoadMoreListView extends LoadMoreListView{

    public ScrollLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollLoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
