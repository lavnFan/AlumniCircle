package com.seu.wufan.alumnicircle.ui.activity.me;

import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.common.base.BaseSwipeActivity;
import com.seu.wufan.alumnicircle.common.base.BasisAdapter;
import com.seu.wufan.alumnicircle.ui.adapter.me.CollectionItemAdapter;
import com.seu.wufan.alumnicircle.ui.widget.LoadMoreListView;
import com.umeng.comm.ui.fragments.FavoritesFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author wufan
 * @date 2016/2/13
 */
public class MyCollectionActivity extends BaseSwipeActivity {

    FavoritesFragment favoritesFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void prepareDatas() {
        setToolbarTitle(getResources().getString(R.string.my_collection));
    }

    @Override
    protected void initViewsAndEvents() {
        if(favoritesFragment==null){
            favoritesFragment = FavoritesFragment.newFavoritesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.collection_container, favoritesFragment).commit();
        }
    }


    @Override
    protected View getLoadingTargetView() {
        return null;
    }

}