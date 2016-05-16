package com.seu.wufan.alumnicircle.mvp.views.activity.me;

import com.seu.wufan.alumnicircle.api.entity.item.Job;
import com.seu.wufan.alumnicircle.mvp.views.IView;

/**
 * @author wufan
 * @date 2016/5/15
 */
public interface IJobShowView extends IView{

    void backEdit(Job job,int REQUEST_CODE);
}
