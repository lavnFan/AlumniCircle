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

package com.umeng.common.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.sdkmanager.ImagePickerManager;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.emoji.EmojiBean;
import com.umeng.common.ui.emoji.EmojiBorad;
import com.umeng.common.ui.imagepicker.PhotoSelectorActivity;
import com.umeng.common.ui.mvpview.MvpCommentView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;
import com.umeng.common.ui.presenter.impl.CommentPresenter;
import com.umeng.common.ui.presenter.impl.TakePhotoPresenter;
import com.umeng.common.ui.widgets.CommentEditText;
import com.umeng.common.ui.widgets.SquareImageView;

import java.util.List;


/**
 * 含有评论发布页面的Fragment基类
 * 
 * @param <T> 数据类型
 * @param <P> Presenter类型
 */
public abstract class CommentEditFragment<T, P extends BaseFragmentPresenter<T>> extends
        BaseFragment<T, P> implements MvpCommentView {
    /**
     * 评论布局的根视图
     */
    protected View mCommentLayout;
    /**
     * 评论内容编辑框
     */
    protected CommentEditText mCommentEditText;
    /**
     * 发送按钮
     */
    protected View mCommentSendView;
    /**
     * 输入法
     */
    protected InputMethodManager mInputMgr;
    /**
     * 当前的Feed
     */
    protected FeedItem mFeedItem;
    /**
     * 评论的Presenter
     */
    protected CommentPresenter mCommentPresenter;
    /**
     * emoji表情触发按钮
     */
    protected ImageView mEmojiImageView;
    protected TextView mCharLimit;
    /**
     * 选择图片按钮
     */
    protected View mPickImgBtn;
    /**
     * 对某人进行回复。用在评论的时候显示在EditText中
     */
    protected CommUser mReplyUser;
    /** 对某条评论进行回复 */
    protected String mReplyCommentId = "";
    /**
     * emoji表情面板
     */
    protected EmojiBorad mEmojiBoard;

    protected int totalTime = 0;
    protected boolean isFinish = false;
    protected int mKeyboardIconRes;
    protected int mEmojiIconRes;
    protected BaseInputConnection mInputConnection = null;
    /**
     * 处理拍照逻辑的present
     */
    protected TakePhotoPresenter mTakePhotoPresenter = new TakePhotoPresenter();
    protected SquareImageView mSelectedImgView;
    protected View mDeleteBtn;
    protected String mImagePath;

    @Override
    protected void initWidgets() {
        mCommentLayout = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_comment_edit_layout"));
        mCommentLayout.setEnabled(false);
        mCharLimit = mViewFinder.findViewById(ResFinder
                .getId("charlimit"));

        mCommentEditText = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_comment_edittext"));
        mCommentEditText.setCharDisplay(mCharLimit);
        mCommentEditText.setGravity(Gravity.LEFT & Gravity.TOP);
        mCommentEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mCommentEditText.setSingleLine(false);
        mCommentEditText.setHorizontallyScrolling(false);
        mCommentEditText.setEditTextBackListener(new CommentEditText.EditTextBackEventListener() {

            @Override
            public boolean onClickBack() {
                hideCommentLayout();
                mEmojiBoard.setVisibility(View.GONE);
                return true;
            }
        });
        mCommentEditText.setTextChangeListener(new CommentEditText.OnTextChangeListener() {
            @Override
            public void onTextChange(String str) {
                if((TextUtils.isEmpty(str) && TextUtils.isEmpty(mImagePath)) || str.length() > CommConfig.getConfig().mCommentLen){
                    mCommentSendView.setEnabled(false);
                }else{
                    mCommentSendView.setEnabled(true);
                }
            }
        });

        // 点击评论编辑框时，此时将弹出软键盘，需要隐藏掉表情面板
        mCommentEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mEmojiBoard.setVisibility(View.GONE);
            }
        });

        mSelectedImgView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_image_selected"));
        mSelectedImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        });
        Drawable drawable = ResFinder.getDrawable("umeng_comm_add_image");
        mSelectedImgView.setImageDrawable(drawable);

        mDeleteBtn = mViewFinder.findViewById(ResFinder.getId("umeng_comm_image_delete"));
        mDeleteBtn.setVisibility(View.GONE);
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedImg();
            }
        });

        mInputConnection = new BaseInputConnection(mCommentEditText, true);
        mCommentSendView = mViewFinder.findViewById(ResFinder
                .getId("umeng_comm_comment_send_button"));
        mCommentSendView.setClickable(true);
        mCommentSendView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String content = mCommentEditText.getText().toString();
                if (!checkCommentData(content)) {
                    return;
                }
                hideCommentLayout();
                postComment(content);
                clearCommentData();
            }
        });

        mViewFinder.findViewById(ResFinder.getId("umeng_comm_comment_cancel_btn"))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearCommentData();
                        hideCommentLayout();
                    }
                });

        mEmojiImageView = mViewFinder.findViewById(ResFinder.getId("umeng_comm_emoji"));
        mEmojiBoard = mViewFinder.findViewById(ResFinder.getId("umeng_comm_emojiview"));
        mKeyboardIconRes = ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_emoji_keyboard");
        mEmojiIconRes = ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_emoji");

        // click emoji ImageView to show emoji board
        mEmojiImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEmojiBoard.getVisibility() == View.VISIBLE) { // 显示输入法，隐藏表情board
                    mEmojiBoard.setVisibility(View.GONE);
                    mEmojiImageView.setImageResource(mEmojiIconRes);
                    getActivity().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    sendInputMethodMessage(Constants.INPUT_METHOD_SHOW, mCommentEditText);
                } else { // 隐藏输入法，显示表情board
                    mEmojiImageView.setImageResource(mKeyboardIconRes);
                    sendInputMethodMessage(Constants.INPUT_METHOD_DISAPPEAR, mCommentEditText);
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mEmojiBoard.setVisibility(View.VISIBLE);
                        }
                    }, 80);
                }
            }
        });

        // 点击表情的某一项的回调函数
        mEmojiBoard.setOnEmojiItemClickListener(new EmojiBorad.OnEmojiItemClickListener() {

            @Override
            public void onItemClick(EmojiBean emojiBean) {
                // delete event
                if (EmojiBorad.DELETE_KEY.equals(emojiBean.getEmoji())) {
                    // 对于删除事件，此时模拟一个输入法上的删除事件达到删除的效果
                    //【注意：此处不能调用delete方法，原因是emoji有些是单字符，有的是双字符】
                    mInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL));
                    return;
                }
                // 预判断，如果插入超过140字符，则不显示最新的表情
                int emojiLen = emojiBean.isDouble ? 2: 1;
                if ( mCommentEditText.getText().length()+emojiLen > Constants.COMMENT_CHARS ) {
                    ToastMsg.showShortMsgByResName("umeng_comm_comment_text_max");
                    return ;
                }
                int start = mCommentEditText.getSelectionStart();
                int end = mCommentEditText.getSelectionEnd();
                if (start < 0) {
                    mCommentEditText.append(emojiBean.getEmoji());
                } else {
                    mCommentEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                            emojiBean.getEmoji(), 0, emojiBean.getEmoji().length());
                }
            }
        });

        // 此时如果点击其它区域，需要隐藏表情面板
        mCommentEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mEmojiBoard.setVisibility(View.GONE);
                }
            }
        });

        mInputMgr = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }

    protected void jumpToPickImagesPage(){
        Intent intent = new Intent(getActivity(), PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorActivity.KEY_MAX, 1);
        // 传递已经选中的图片
//        intent.putStringArrayListExtra(Constants.PICKED_IMAGES, selected);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivityForResult(intent, Constants.PICK_IMAGE_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQ_CODE) {
                if (data != null && data.getExtras() != null) {
                    // 获取选中的图片
                    List<String> selectedList = ImagePickerManager.getInstance().getCurrentSDK()
                            .parsePickedImageList(data);
                    if(selectedList != null && selectedList.size() > 0){
                        showSelectedImg(selectedList.get(0));
                    }
                }
            }else if(requestCode == TakePhotoPresenter.REQUEST_IMAGE_CAPTURE){
                String imgUri = mTakePhotoPresenter.updateImageToMediaLibrary();
                showSelectedImg(imgUri);
            }
        }
    }

    protected void showSelectedImg(String path){
        if(TextUtils.isEmpty(path)){
            return;
        }
        mSelectedImgView.setImageUrl(path);
        mSelectedImgView.setClickable(false);
        mDeleteBtn.setVisibility(View.VISIBLE);
        mImagePath = path;
        mCommentSendView.setEnabled(true);
    }

    protected void deleteSelectedImg(){
        mSelectedImgView.setClickable(true);
        Drawable drawable = ResFinder.getDrawable("umeng_comm_add_image");
        mSelectedImgView.setImageDrawable(drawable);
        mDeleteBtn.setVisibility(View.GONE);
        mImagePath = "";
        String str = mCommentEditText.getText().toString();
        if(TextUtils.isEmpty(str) || str.length() > CommConfig.getConfig().mCommentLen){
            mCommentSendView.setEnabled(false);
        }else{
            mCommentSendView.setEnabled(true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mTakePhotoPresenter.attach(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTakePhotoPresenter.detach();
    }

    /**
     * 该Handler主要处理软键盘的弹出跟隐藏
     */
    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            View view = (View) msg.obj;
            // 显示软键盘
            if (msg.what == Constants.INPUT_METHOD_SHOW) {
                boolean result = mInputMgr.showSoftInput(view, 0);
                if (!result && totalTime < Constants.LIMIT_TIME) {
                    totalTime += Constants.IDLE;
                    Message message = Message.obtain(msg);
                    mHandler.sendMessageDelayed(message, Constants.IDLE);
                } else if (!isFinish) {
                    totalTime = 0;
                    result = view.requestFocus();
                    isFinish = true;
                }
            } else if (msg.what == Constants.INPUT_METHOD_DISAPPEAR) {
                // 隐藏软键盘
                mInputMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    };

    /**
     * 发送show or hide输入法消息</br>
     * 
     * @param type
     * @param view
     */
    protected void sendInputMethodMessage(int type, View view) {
        Message message = mHandler.obtainMessage(type);
        message.obj = view;
        mHandler.sendMessage(message);
    }

    protected void clearCommentData(){
        mCommentEditText.setText("");
        deleteSelectedImg();
    }

    public EmojiBorad getmEmojiBoard() {
        return mEmojiBoard;
    }

    @Override
    protected P createPresenters() {
        // 初始化评论相关的Presenter
        mCommentPresenter = new CommentPresenter(this, mFeedItem);
        mCommentPresenter.attach(getActivity());
        return null;
    }

    protected void postComment(String text) {
        if (mCommentPresenter == null) {
            mCommentPresenter = new CommentPresenter(this, mFeedItem);
            mCommentPresenter.attach(getActivity());
        }
        if(TextUtils.isEmpty(mImagePath)){
            mCommentPresenter.postComment(text, mReplyUser, mReplyCommentId);
        }else{
            mCommentPresenter.postComment(text, mImagePath, mReplyUser, mReplyCommentId);
        }
    }

    protected void showCommentLayout() {
        mCommentLayout.setVisibility(View.VISIBLE);
        mCommentLayout.setClickable(true);
        mCommentEditText.requestFocus();

        mCommentEditText.postDelayed(new Runnable() {

            @Override
            public void run() {
                mInputMgr.showSoftInput(mCommentEditText, 0);
            }
        }, 30);
        
    }
    public boolean IsCommentLayoutShow(){
        if (mCommentLayout.getVisibility() == View.VISIBLE){
            return true;
        }else {
            return false;
        }
    }
    @Override
    public void showCommentLayout(int realPosition, Comment comment) {
        showCommentLayout();
    }

    protected void hideCommentLayout() {
        mCommentLayout.setVisibility(View.GONE);
        mEmojiBoard.setVisibility(View.GONE);
//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mInputMgr.hideSoftInputFromWindow(mCommentEditText.getWindowToken(), 0);
    }

    /**
     * 检查评论是否有效。目前仅仅判空</br>
     * 
     * @param content 评论的内容
     * @return
     */
    protected boolean checkCommentData(String content) {
        // 检查评论的内容是否合法
        if (TextUtils.isEmpty(content)&&TextUtils.isEmpty(mImagePath)) {
            ToastMsg.showShortMsgByResName("umeng_comm_content_invalid");
            return false;
        }
        if (content.length() > Constants.COMMENT_CHARS) {
            ToastMsg.showShortMsgByResName("umeng_comm_comment_text_overflow");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        if (mCommentPresenter != null) {
            mCommentPresenter.detach();
        }
        super.onDestroy();
    }

    @Override
    public void postCommentSuccess(Comment comment, CommUser replyUser) {
        deleteSelectedImg();
    }

    @Override
    public void loadMoreComment(List<Comment> comments) {
    }

    @Override
    public void onRefreshEnd() {

    }

    @Override
    public void onCommentDeleted(Comment comment) {

    }



    protected void showSelectDialog() {
        jumpToPickImagesPage();
//        final Dialog selectDialog = new Dialog(getActivity(),
//                ResFinder.getStyle("umeng_comm_action_dialog_fullscreen"));
//        selectDialog.setCanceledOnTouchOutside(true);
//        selectDialog.setContentView(ResFinder.getLayout("umeng_comm_report_reply_comment_dialog"));
//
//        TextView galleryTextView = (TextView) selectDialog.findViewById(ResFinder
//                .getId("umeng_comm_report_comment_tv"));
//        galleryTextView.setText("图库");
//        galleryTextView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                selectDialog.dismiss();
//                jumpToPickImagesPage();
//            }
//        });
//        TextView cameraTextView = (TextView) selectDialog.findViewById(ResFinder
//                .getId("umeng_comm_reply_comment_tv"));
//        cameraTextView.setText("相机");
//        cameraTextView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                selectDialog.dismiss();
//                mTakePhotoPresenter.takePhoto();
//            }
//        });
//        selectDialog.show();
    }

}
