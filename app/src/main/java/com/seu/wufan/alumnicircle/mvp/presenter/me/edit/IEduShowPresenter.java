package com.seu.wufan.alumnicircle.mvp.presenter.me.edit;

import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.mvp.presenter.IPresenter;

/**
 * @author wufan
 * @date 2016/5/15
 */
public interface IEduShowPresenter extends IPresenter{

    void saveEdu(Edu edu);

    void updateEdu(String id,Edu edu);

}
