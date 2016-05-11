package com.umeng.common.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpFeedDetailActivityView;
import com.umeng.common.ui.presenter.impl.FeedDetailActivityPresenter;


/**
 * Created by wangfei on 16/1/27.
 */
public class FeedListActionDialog extends ActionDialog{
    FeedDetailActivityPresenter mPresenter;

    public FeedListActionDialog(Context context) {
        super(context);
        mPresenter = new FeedDetailActivityPresenter();
        mPresenter.attach(context);
    }

    public void attachView(MvpFeedDetailActivityView view) {
        mPresenter.setActivityView(view);
    }

    public void setFeedId(String feedId) {
        mFeedItem = new FeedItem();
        mFeedItem.id = feedId;
        this.setFeedItem(mFeedItem);
    }

    @Override
    protected void initViewClickListeners() {
        super.initViewClickListeners();
        // 自己不能举报自己
        if( mFeedItem == null || CommConfig.getConfig().loginedUser.id.equals(mFeedItem.creator.id) ) {
            mReportView.setVisibility(View.GONE);
        }

        mCopyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                copyToClipboard();
                FeedListActionDialog.this.dismiss();
            }
        });

//        mDeleteView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                FeedListActionDialog.this.dismiss();
//                mPresenter.showDeleteConfirmDialog();
//            }
//        });
//        mSaveView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               dismiss();
//                mPresenter.favoritesFeed();
//            }
//        });

        mDeleteView.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void onStart(View v) {
                dismiss();
            }
            @Override
            protected void doAfterLogin(View v) {
                dismiss();
                mPresenter.showDeleteConfirmDialog();
            }
        });
        mSaveView.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void onStart(View v) {
                dismiss();
            }

            @Override
            public void doAfterLogin(View v) {
                dismiss();
                if(mFeedItem.isCollected){
                    mPresenter.cancelFavoritesFeed();
                }else{
                    mPresenter.favoritesFeed();
                }
            }
        });

//        changeBackground();
    }

//    /**
//     *
//     * 在显示 or 隐藏 某些View时，需要改变他们的背景</br>
//     */
//    private void changeBackground(){
//        if ( mReportView.getVisibility() == View.GONE ) {
//            mDeleteView.setBackgroundResource(ResFinder.getResourceId(ResFinder.ResType.DRAWABLE, "umeng_comm_more_radius_top"));
//            mCopyView.setBackgroundResource(ResFinder.getResourceId(ResFinder.ResType.DRAWABLE, "umeng_comm_more_radius_bottom"));
//        } else {
//            mDeleteView.setBackgroundResource(ResFinder.getResourceId(ResFinder.ResType.DRAWABLE, "umeng_comm_more_radius_none"));
//        }
//
//        if ( mDeleteView.getVisibility() == View.GONE ) {
//            mReportView.setBackgroundResource(ResFinder.getResourceId(ResFinder.ResType.DRAWABLE, "umeng_comm_more_radius_top"));
//            mCopyView.setBackgroundResource(ResFinder.getResourceId(ResFinder.ResType.DRAWABLE, "umeng_comm_more_radius_bottom"));
//        }
//    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSaveView.setVisibility(View.VISIBLE);
        mShareView.setVisibility(View.VISIBLE);
        if (!isReportable()){
            mReportView.setVisibility(View.GONE);
            mReportUser.setVisibility(View.GONE);
        }else {
            mReportView.setVisibility(View.VISIBLE);
            mReportUser.setVisibility(View.VISIBLE);
        }
        if (!isDeleteable()) {
            mDeleteView.setVisibility(View.GONE);
            mReportView.setVisibility(View.VISIBLE);
        } else {
            mDeleteView.setBackgroundColor(Color.WHITE);
            mReportView.setVisibility(View.GONE);
//            mReportUser.setVisibility(View.GONE);
        }
//        if(!isController()){
            mSetRecommened.setVisibility(View.GONE);
//        }
//        if (!CommonUtils.isLogin(mContext)){
//             mReportView.setVisibility(View.GONE);
//             mDeleteView.setVisibility(View.GONE);
//             mCopyView.setVisibility(View.GONE);
//             mSaveView.setVisibility(View.GONE);
//             mReportUser.setVisibility(View.GONE);
//        }
    }

    public void setFeedItem(FeedItem feedItem) {
        mFeedItem = feedItem;
        mPresenter.setFeedItem(feedItem);
        // 自己不能举报自己.[注意：考虑来自于推送的情况]
        if( mFeedItem.creator != null && CommConfig.getConfig().loginedUser.id.equals(mFeedItem.creator.id) ) {
            mReportView.setVisibility(View.GONE);
        } else {
            mReportView.setVisibility(View.VISIBLE);
        }
        updateFavoriteBtnState();
//        changeBackground();
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
        boolean hasDeletePermission = loginedUser.permisson == CommUser.Permisson.ADMIN // 管理员删除内容权限
                && loginedUser.subPermissions.contains(CommUser.SubPermission.DELETE_CONTENT);
        return deleteable || hasDeletePermission;
    }
    private boolean isReportable() {
        CommUser loginedUser = CommConfig.getConfig().loginedUser;
        if (!loginedUser.id.equals(mFeedItem.creator.id)){
            return true;
        }
        else{
            return false;
        }

    }
    private boolean isController() {
        CommUser loginedUser = CommConfig.getConfig().loginedUser;
        if (loginedUser.permisson == CommUser.Permisson.ADMIN){
            return true;
        }
        else{
            return false;
        }

    }
    @Override
    protected void report() {
        String loginedUid = CommConfig.getConfig().loginedUser.id;
        if (mFeedItem.creator.id.equals(loginedUid)) {
            ToastMsg.showShortMsgByResName("umeng_comm_do_not_spam_yourself_content");
            return;
        }
        mPresenter.showReportConfirmDialog();
    }

    @Override
    protected void reportUser() {
        mPresenter.showReportUserConfirmDialog();
    }
}
