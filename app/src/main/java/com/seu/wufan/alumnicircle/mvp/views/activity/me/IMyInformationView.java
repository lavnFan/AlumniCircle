package com.seu.wufan.alumnicircle.mvp.views.activity.me;

import com.seu.wufan.alumnicircle.api.entity.UserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.views.IView;

/**
 * @author wufan
 * @date 2016/5/11
 */
public interface IMyInformationView extends IView{

    void initMyInfo(UserInfoDetailRes res);

    void initMyInfo(UserInfoRes res);

}
