package com.seu.wufan.alumnicircle.mvp.presenter.circle;

import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

import java.util.List;

/**
 * @author wufan
 * @date 2016/4/29
 */
public interface IPublishDynamicIPresenter extends IPresenter {

    void publishDynamic(int count,String news_text, List<String>images,String topic_id);

}
