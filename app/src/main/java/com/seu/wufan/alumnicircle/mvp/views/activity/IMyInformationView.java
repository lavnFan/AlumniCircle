package com.seu.wufan.alumnicircle.mvp.views.activity;

import com.seu.wufan.alumnicircle.api.entity.GetUserInfoDetailRes;
import com.seu.wufan.alumnicircle.api.entity.UserInfoRes;
import com.seu.wufan.alumnicircle.mvp.views.IView;
import com.tencent.connect.UserInfo;

/**
 * @author wufan
 * @date 2016/5/11
 */
public interface IMyInformationView extends IView{

    void initMyInfo(GetUserInfoDetailRes res);

    void initMyInfo(UserInfoRes res);

}
