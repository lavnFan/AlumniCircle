package com.seu.wufan.alumnicircle.mvp.views.activity;

import com.seu.wufan.alumnicircle.api.entity.item.SearchFriendItem;
import com.seu.wufan.alumnicircle.mvp.views.IView;

import java.util.List;

/**
 * @author wufan
 * @date 2016/5/22
 */
public interface ISearchView extends IView{
    void showUserResult(List<SearchFriendItem> searchFriendItems);
}
