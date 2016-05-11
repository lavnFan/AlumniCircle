package com.umeng.common.ui.mvpview;

import com.umeng.comm.core.beans.Category;
import com.umeng.comm.core.beans.Topic;

import java.util.List;

/**
 * Created by wangfei on 15/11/24.
 */
public interface MvpCategoryView extends MvpBaseRefreshView{
    public List<Category> getBindDataSource();

    public void notifyDataSetChanged();

    /**
     * 刷新结束，仅仅hide相关View，不做数据相关操作</br>
     */
    public void onRefreshEndNoOP();

    public void ChangeAdapter(List<Topic> list);
}
