package com.umeng.common.ui.mvpview;

import com.umeng.comm.core.beans.BaseBean;

/**
 * Created by umeng on 12/11/15.
 */
public interface MvpRecyclerView<T extends BaseBean> extends MvpBaseRefreshView{

    public void onDataSetChanged();

    public void showProgressFooter();

    public void hideProgressFooter();

}
