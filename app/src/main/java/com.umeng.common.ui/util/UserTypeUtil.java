package com.umeng.common.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.common.ui.widgets.NetworkImageView;


/**
 * Created by wangfei on 16/1/28.
 */
public class UserTypeUtil {
    public static void SetUserType(Context context, CommUser user,LinearLayout layout){
        if (layout!=null) {
            layout.removeAllViews();
        }
        if (user!=null&&user.medals!=null&&user.medals.size()!=0){

            NetworkImageView img= new NetworkImageView(context);
            img.setImageUrl(user.medals.get(0).imgUrl);
//            img.setMaxHeight(CommonUtils.dip2px(context, 15));
//            img.setMaxWidth(CommonUtils.dip2px(context,15));
            int height = CommonUtils.dip2px(context,15);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
            layout.setVisibility(View.VISIBLE);
            layout.addView(img, lp);
        }
    }
}
