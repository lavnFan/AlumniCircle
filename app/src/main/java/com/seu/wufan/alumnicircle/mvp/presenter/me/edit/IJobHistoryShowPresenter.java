package com.seu.wufan.alumnicircle.mvp.presenter.me.edit;

import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/15
 */
public interface IJobHistoryShowPresenter extends IPresenter{

    void saveJob(Job job);

    void updateJob(String id,Job job);

}
