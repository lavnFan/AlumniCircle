package com.umeng.comm.ui.adapters.viewholders;

import android.widget.TextView;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.viewholders.ViewHolder;
import com.umeng.common.ui.widgets.RoundImageView;


/**
 * Created by wangfei on 16/1/25.
 */
public class FeedLikeViewHolder extends ViewHolder {
    public RoundImageView usericon;
    public TextView username;
    @Override
    protected int getItemLayout() {
        return ResFinder.getLayout("umeng_comm_feed_like_layout");
    }
    @Override
    protected void initWidgets() {
        usericon = findViewById(ResFinder
                .getId("like_user_icon"));
        username = findViewById(ResFinder
                .getId("like_user_name"));

    }
}
