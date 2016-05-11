/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.common.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.CommUser.Gender;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.imageloader.UMImageLoader;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.viewholders.ActiveUserViewHolder;
import com.umeng.common.ui.listener.LikeonClickListener;


/**
 * 活跃用户Adapter
 */
public class ActiveUserAdapter extends
        CommonAdapter<CommUser, ActiveUserViewHolder> {

    private String mDynamicStr = ResFinder.getString("umeng_comm_user_post_dynamic");
    private String mFansStr = ResFinder.getString("umeng_comm_fans_num");

    private RecommendTopicAdapter.FollowListener<CommUser> mFollowListener;
    private UMImageLoader mImageLoader;

    private static final String DIVIDER = "       ";
    private LikeonClickListener likeonClickListener;
    private boolean isFromFindPage;// 是否来自于发现页面。如果来自发现页面，则需要单独的逻辑处理

    /**
     * @param context
     */
    public ActiveUserAdapter(Context context) {
        super(context);
        mImageLoader = ImageLoaderManager.getInstance().getCurrentSDK();
    }

    @Override
    protected ActiveUserViewHolder createViewHolder() {
        return new ActiveUserViewHolder();
    }

    @Override
    protected void setItemData(int position, final ActiveUserViewHolder viewHolder, View rootView) {
        final CommUser user = getItem(position);
        // 用户名
        viewHolder.mUserNameTextView.setText(user.name);
        // 用户头像
        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
        if (!TextUtils.isEmpty(user.iconUrl)) {
            mImageLoader.displayImage(user.iconUrl, viewHolder.mImageView, option);
        } else {
            viewHolder.mImageView.setImageResource(option.mLoadingResId);
        }
        setupItemClickListener(viewHolder.mImageView, user);
        // 用户性别
        String genderRes;
        if (user.gender == Gender.MALE) {
            genderRes = "umeng_comm_user_info_male";//umeng_comm_gender_male
//            genderResId = ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_gender_male");
        } else {
            genderRes = "umeng_comm_user_info_female";//umeng_comm_gender_female
//            genderResId = ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_gender_female");
        }
        viewHolder.mToggleButton.setBackgroundDrawable(ResFinder.getDrawable("umeng_comm_focus_user_bg"));
        viewHolder.mToggleButton.setTextOn("");
        viewHolder.mToggleButton.setTextOff("");
//        viewHolder.mGenderImageView.setImageResource(genderResId);
        Drawable genderImg = ResFinder.getDrawable(genderRes);
        genderImg.setBounds(0, 0, CommonUtils.dip2px(mContext, 10), CommonUtils.dip2px(mContext, 10));
        viewHolder.mUserNameTextView.setCompoundDrawables(null, null, genderImg, null);

        // 粉丝数
        String text = buildMsgFansStr(user);
        viewHolder.mMsgFansTextView.setText(text);

        // 关注状态
        boolean followStatus = user.extraData.getBoolean(Constants.IS_FOCUSED);
        setFollowStatus(viewHolder, user, followStatus);
        // 跳转到用户中心页面
        setupItemClickListener(viewHolder.mView, user);
    }

    /**
     * 是否来自于发现页面</br>
     * 
     * @param fromFindPage
     */
    public void setFromFindPage(boolean fromFindPage) {
        isFromFindPage = fromFindPage;
    }

    public void setFollowListener(RecommendTopicAdapter.FollowListener<CommUser> listener) {
        this.mFollowListener = listener;
    }

    public void setFollowStatus(final ActiveUserViewHolder viewHolder, final CommUser user,
            boolean status) {
        viewHolder.mToggleButton.setChecked(status);
        viewHolder.mToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mFollowListener.onFollowOrUnFollow(user, viewHolder.mToggleButton,
                        viewHolder.mToggleButton.isChecked());
            }
        });
    }

    public void setupItemClickListener(View rootView, final CommUser user) {
        if (!isFromFindPage) {
            return;
        }
        rootView.setOnClickListener(
//                new Listeners.LoginOnViewClickListener() {
//
//            @Override
//            protected void doAfterLogin(View v) {
//                Intent intent = new Intent();
//                ComponentName componentName = new ComponentName(mContext,
//                        UserInfoActivity.class);
//                intent.setComponent(componentName);
//                intent.putExtra(Constants.TAG_USER, user);
//                ((Activity) mContext).startActivity(intent);
//            }
//        }//// TODO: 16/1/22 fanshe 
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeonClickListener.onClickListener(user);
//                                        Intent intent = new Intent();
//                ComponentName componentName = new ComponentName(mContext,
//                        UserInfoActivity.class);
//                intent.setComponent(componentName);
//                intent.putExtra(Constants.TAG_USER, user);
//                ((Activity) mContext).startActivity(intent);
                    }
                }
        );
    }

    public void setLikeonClickListener(LikeonClickListener likeonClickListener) {
        this.likeonClickListener = likeonClickListener;
    }

    private String buildMsgFansStr(CommUser user) {
        StringBuilder builder = new StringBuilder(mDynamicStr).append(" ");
        int feedCount = user.feedCount;
        String feedCountStr = CommonUtils.getLimitedCount(feedCount);
        builder.append(feedCountStr);

        builder.append(DIVIDER).append(mFansStr);

        int fansCount = user.fansCount;
        String fansCountStr = CommonUtils.getLimitedCount(fansCount);
        builder.append(fansCountStr);
        return builder.toString();
    }

}
