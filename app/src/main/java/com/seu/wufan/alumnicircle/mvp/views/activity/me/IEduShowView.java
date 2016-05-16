package com.seu.wufan.alumnicircle.mvp.views.activity.me;

import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.mvp.views.IView;

/**
 * @author wufan
 * @date 2016/5/15
 */
public interface IEduShowView extends IView{

    void backEdit(Edu edu, int REQUEST_CODE);

}
