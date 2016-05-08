package com.seu.wufan.alumnicircle.mvp.views.activity;

import com.seu.wufan.alumnicircle.api.entity.DynamicListRes;
import com.seu.wufan.alumnicircle.api.entity.TopicRes;
import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;
import com.seu.wufan.alumnicircle.mvp.views.IView;

import java.util.List;

/**
 * @author wufan
 * @date 2016/4/26
 */
public interface ICircleView extends IView{

    void initTopic(TopicRes res);

    void showDynamic(List<DynamicItem> listRes, boolean firstPage);

}
