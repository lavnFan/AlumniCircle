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

package com.umeng.common.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.CommUser.Permisson;
import com.umeng.comm.core.beans.CommUser.SubPermission;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.FeedDetailActivityPresenter;

import java.util.List;

/**
 * Feed详情页的举报、删除、拷贝的抽象Dialog
 */
public abstract class ActionDialog extends Dialog {

    public FeedItem mFeedItem;
    FeedDetailActivityPresenter mPresenter;
    CommunitySDK mCommunitySDK;
    DatabaseAPI mDatabaseAPI;
    public View mReportView;
    public View mDeleteView;
    public View mCopyView;
    public View mSaveView;
    public View mShareView;
    public View mReportUser;
    public View mSetRecommened;
    Context mContext;

    public TextView mFavoriteTextView;

    protected Window window = null;

    public ActionDialog(Context context) {
        this(context, ResFinder.getStyle("umeng_comm_action_dialog_fullscreen"));
    }

    public ActionDialog(Context context, int theme) {
        super(context, theme);
        mPresenter = new FeedDetailActivityPresenter();
        mPresenter.attach(context);
        mContext = context;
        mCommunitySDK = CommunityFactory.getCommSDK(getContext());
        mDatabaseAPI = DatabaseAPI.getInstance();
        initDialog();
        initViewClickListeners();
    }

    @Override
    public void show() {
        window = getWindow(); //得到对话框
        window.setWindowAnimations(ResFinder.getStyle("dialogWindowAnim")); //设置窗口弹出动画
        super.show();
    }

    @Override
    public void dismiss() {
        window = getWindow(); //得到对话框
        window.setWindowAnimations(ResFinder.getStyle("dialogWindowAnim")); //设置窗口弹出动画
        super.dismiss();
    }

    private void initDialog() {
        this.setContentView(ResFinder.getLayout("umeng_comm_more_dialog_layout"));
        this.setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    protected void initViewClickListeners() {
        mReportView = findViewById(ResFinder.getId("umeng_comm_report_layout"));
        mReportView.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void onStart(View v) {
                dismiss();
            }

            @Override
            protected void doAfterLogin(View v) {
                ActionDialog.this.dismiss();
                report();
            }
        });

        mCopyView = findViewById(ResFinder.getId("umeng_comm_copy_layout"));
        mDeleteView = findViewById(ResFinder.getId("umeng_comm_delete_layout"));

        View cancelView = findViewById(ResFinder.getId("umeng_comm_cancel_layout"));
        cancelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActionDialog.this.dismiss();
            }
        });

        mSaveView = findViewById(ResFinder.getId("umeng_comm_savepost_layout"));
        mFavoriteTextView = (TextView) findViewById(ResFinder.getId("umeng_comm_savepost_tv"));

        mShareView = findViewById(ResFinder.getId("umeng_comm_share_layout"));
        mShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareContent shareItem = new ShareContent();
                shareItem.mText = mFeedItem.text;
                List<ImageItem> imageItems = mFeedItem.imageUrls;
                if (mFeedItem.sourceFeed != null) {
                    imageItems = mFeedItem.sourceFeed.imageUrls;
                }
                if (imageItems.size() > 0) {
                    shareItem.mImageItem = imageItems.get(0);
                }
                shareItem.mTargetUrl = mFeedItem.shareLink;
                if (TextUtils.isEmpty(shareItem.mTargetUrl) && mFeedItem.sourceFeed != null) {
                    shareItem.mTargetUrl = mFeedItem.sourceFeed.shareLink;
                }
                shareItem.mFeedId = mFeedItem.id;
                shareItem.mTitle = mFeedItem.text;
                ShareSDKManager.getInstance().getCurrentSDK().share((Activity) mContext, shareItem);
                ActionDialog.this.dismiss();

            }
        });
        mReportUser = findViewById(ResFinder.getId("umeng_comm_userreport_layout"));
        mReportUser.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void onStart(View v) {
                dismiss();
            }

            @Override
            public void doAfterLogin(View v) {
                ActionDialog.this.dismiss();
                reportUser();
            }
        });
        mSetRecommened = findViewById(ResFinder.getId("umeng_comm_recommend_layout"));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSaveView.setVisibility(View.VISIBLE);
        mShareView.setVisibility(View.VISIBLE);
        if (!isDeleteable()) {
            mDeleteView.setVisibility(View.GONE);
            mReportView.setVisibility(View.VISIBLE);
            mReportUser.setVisibility(View.VISIBLE);
        } else {
            mDeleteView.setBackgroundColor(Color.WHITE);
            // deleteView.setVisibility(View.VISIBLE);
//            mReportView.setVisibility(View.GONE);
            if (CommConfig.getConfig().loginedUser.permisson.equals(Permisson.ADMIN) || CommConfig.getConfig().loginedUser.permisson.equals(Permisson.SUPPER_ADMIN)) {
                mSetRecommened.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void copyToClipboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipData data = ClipData.newPlainText("feed_text", mFeedItem.text);
            android.content.ClipboardManager mClipboard = (android.content.ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            mClipboard.setPrimaryClip(data);
        } else {
            android.text.ClipboardManager mClipboard = (android.text.ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            mClipboard.setText(mFeedItem.text);
        }
    }

    /**
     * 是否可删除该feed。可删除的条件是自己的feed、管理员有删除内容的权限</br>
     *
     * @return
     */
    private boolean isDeleteable() {
        CommUser loginedUser = CommConfig.getConfig().loginedUser;
        boolean deleteable = mFeedItem != null && loginedUser.id.equals(mFeedItem.creator.id); // 自己的feed情况
        boolean hasDeletePermission = loginedUser.permisson == Permisson.ADMIN // 管理员删除内容权限
                && loginedUser.subPermissions.contains(SubPermission.DELETE_CONTENT);
        return deleteable || hasDeletePermission;
    }

    protected abstract void report();

    protected abstract void reportUser();

    public void updateFavoriteBtnState() {
        String str;
        if (!mFeedItem.isCollected) {
            str = ResFinder.getString("umeng_comm_favorites");
        } else {
            str = ResFinder.getString("umeng_comm_cancel_favorites");
        }
        mFavoriteTextView.setText(str);
    }
}
