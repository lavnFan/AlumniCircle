package com.umeng.comm.ui.presenter.impl;

import android.content.Context;
import android.text.TextUtils;

import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.CommentResponse;
import com.umeng.comm.core.nets.responses.LikesResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpCommentView;
import com.umeng.common.ui.mvpview.MvpFeedDetailView;
import com.umeng.common.ui.mvpview.MvpLikeView;
import com.umeng.common.ui.presenter.impl.LikePresenter;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangfei on 16/1/18.
 */
public class FeedDetailWBPresenter extends BaseFeedWeiboPresenter {
    private MvpFeedDetailView mDetailView;
    private MvpLikeView mLikeView;
    private MvpCommentView mCommentView;
    private FeedItem mFeedItem;
    private String mNextPageUrlcomment;
    private String mNextPageUrllike;
    private LikePresenter mLikePresenter;
//    private volatile AtomicBoolean mUpdateNextPageUrl = new AtomicBoolean(true);


    public FeedDetailWBPresenter(MvpFeedDetailView feedDetailView,
                               MvpLikeView likeView, MvpCommentView commentView, FeedItem feedItem) {
        this.mDetailView = feedDetailView;
        this.mLikeView = likeView;
        this.mCommentView = commentView;
        this.mFeedItem = feedItem;

        mLikePresenter = new LikePresenter(mLikeView);
    }


    @Override
    public void attach(Context context) {
        super.attach(context);
        mLikePresenter.attach(context);
    }

    public void setFeedItem(FeedItem feedItem) {
        this.mFeedItem = feedItem;
        mLikePresenter.setFeedItem(feedItem);
    }

    /**
     * 获取该feed的点赞</br>
     */
    private void loadLikesFromServer() {
        mCommunitySDK.fetchFeedLikes(mFeedItem.id, new Listeners.SimpleFetchListener<LikesResponse>() {
            @Override
            public void onComplete(LikesResponse response) {
                if (NetworkUtils.handleResponseComm(response)) {
                    return;
                }
                if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
//                    ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                    return;
                }
                if (response.errCode != ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_load_failed");
                    return;
                }
                List<Like> likes = response.result;
                likes.removeAll(mFeedItem.likes);
                mFeedItem.likes.addAll(likes);
                mNextPageUrllike = response.nextPageUrl;
                mLikeView.updateLikeView(response.nextPageUrl);
                mCommentView.onRefreshEnd();
                saveLikesToDB(likes);
            }
        });
    }

    /**
     * 保存Like数据到数据库</br>
     *
     * @param likes
     */
    private void saveLikesToDB(List<Like> likes) {
        if (CommonUtils.isListEmpty(likes)) {
            return;
        }
        mDatabaseAPI.getFeedDBAPI().saveFeedToDB(mFeedItem);// 由于like数据变化，此时需要级联更新对应的feed数据
        mDatabaseAPI.getLikeDBAPI().saveLikesToDB(mFeedItem);
    }
    /**
     * 只看楼主评论列表</br>
     */
    public void loadCommentsByuserFromServer() {
        mCommunitySDK.fetchFeedCommentsByuser(mFeedItem.id, mFeedItem.creator.id, Comment.CommentOrder.DESC,
                new Listeners.SimpleFetchListener<CommentResponse>() {
                    @Override
                    public void onComplete(CommentResponse response) {
                        mCommentView.onRefreshEnd();
                        if (NetworkUtils.handleResponseComm(response)) {
                            mDetailView.showOwnerComment(false);
                            return;
                        }

                        if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                            mDetailView.showOwnerComment(false);
                            return;
                        }
                        if (response.errCode != ErrorCode.NO_ERROR) {
                            ToastMsg.showShortMsgByResName("umeng_comm_load_failed");
                            mDetailView.showOwnerComment(false);
                            return;
                        }
                        mNextPageUrlcomment = response.nextPageUrl;
//                    if (TextUtils.isEmpty(mNextPageUrl) && mUpdateNextPageUrl.get()) {
//                        mNextPageUrl = response.nextPageUrl;
//                        mUpdateNextPageUrl.set(false);
//                    }
                        List<Comment> comments = response.result;
                        mFeedItem.comments.clear();
                        mFeedItem.comments.addAll(comments);
                        if (mFeedItem.commentCount == 0) {
                            mFeedItem.commentCount = mFeedItem.comments.size();
                        }
                        sortComments();
                        mCommentView.updateCommentView();
                        mDetailView.showOwnerComment(true);
                    }
                });


    }
    /**
     * 获取feed的评论列表</br>
     */
    public void loadCommentsFromServer() {
        mCommunitySDK.fetchFeedComments(mFeedItem.id, Comment.CommentOrder.DESC,
                new Listeners.SimpleFetchListener<CommentResponse>() {
                    @Override
                    public void onComplete(CommentResponse response) {
                        mCommentView.onRefreshEnd();
                        if (NetworkUtils.handleResponseComm(response)) {
                            mDetailView.showAllComment(false);
                            return;
                        }

                        if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                            mDetailView.showAllComment(false);
                            return;
                        }
                        if (response.errCode != ErrorCode.NO_ERROR) {
                            ToastMsg.showShortMsgByResName("umeng_comm_load_failed");
                            mDetailView.showAllComment(false);
                            return;
                        }
                        mNextPageUrlcomment = response.nextPageUrl;
//                if (TextUtils.isEmpty(mNextPageUrl) && mUpdateNextPageUrl.get()) {
//                    mNextPageUrl = response.nextPageUrl;
//                    mUpdateNextPageUrl.set(false);
//                }
                        List<Comment> comments = response.result;
                        mFeedItem.comments.clear();
                        mFeedItem.comments.addAll(comments);
                        if (mFeedItem.commentCount == 0) {
                            mFeedItem.commentCount = mFeedItem.comments.size();
                        }
                        sortComments();
                        mCommentView.updateCommentView();
                        mDetailView.showAllComment(true);
                        saveCommentsToDB(response.result);

                    }
                });
    }

    public void loadMoreComments() {
        if (TextUtils.isEmpty(mNextPageUrlcomment)) {
            mCommentView.onRefreshEnd();
            return;
        }

        mCommunitySDK.fetchNextPageData(mNextPageUrlcomment, CommentResponse.class,
                new Listeners.SimpleFetchListener<CommentResponse>() {

                    @Override
                    public void onComplete(CommentResponse response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            return;
                        }
                        if (response.errCode == ErrorCode.NO_ERROR) {
                            mNextPageUrlcomment = response.nextPageUrl;
                            mCommentView.loadMoreComment(response.result);
                            saveCommentsToDB(response.result);
                        } else {
                            ToastMsg.showShortMsgByResName("umeng_comm_request_failed");
                        }
                        mCommentView.onRefreshEnd();
                    }
                });
    }
    public void loadMoreLikes() {
        if (TextUtils.isEmpty(mNextPageUrllike)) {
            mCommentView.onRefreshEnd();
            return;
        }

        mCommunitySDK.fetchNextPageData(mNextPageUrllike, LikesResponse.class,
                new Listeners.SimpleFetchListener<LikesResponse>() {

                    @Override
                    public void onComplete(LikesResponse response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            return;
                        }
                        if (response.errCode == ErrorCode.NO_ERROR) {
                            mNextPageUrllike = response.nextPageUrl;
                            mLikeView.loadMoreLike(response.result);
                            saveLikesToDB(response.result);
                        } else {
                            ToastMsg.showShortMsgByResName("umeng_comm_request_failed");
                        }
                        mCommentView.onRefreshEnd();
                    }
                });
    }
    /**
     * 保存评论数据到数据库</br>
     *
     * @param comments
     */
    private void saveCommentsToDB(List<Comment> comments) {
        if (CommonUtils.isListEmpty(comments)) {
            return;
        }
        mDatabaseAPI.getFeedDBAPI().saveFeedToDB(mFeedItem); // 由于comment数据变化，此时需要级联更新对应的feed数据
        mDatabaseAPI.getCommentAPI().saveCommentsToDB(mFeedItem);

    }

    public void postLike(final String feedId) {
        mLikePresenter.postLike(feedId);
    }

    public void postUnlike(final String feedId) {
        mLikePresenter.postUnlike(feedId);
    }

    /**
     * 从数据库中加载Like</br>
     */
    private void loadLikesFromDB() {
        Listeners.SimpleFetchListener<List<Like>> listener = new Listeners.SimpleFetchListener<List<Like>>() {

            @Override
            public void onComplete(List<Like> likes) {
                likes.removeAll(mFeedItem.likes);
                mFeedItem.likes.addAll(likes);
                // mFeedItem.likeCount = mFeedItem.likes.size();
                mLikeView.updateLikeView("");
            }
        };
        mDatabaseAPI.getLikeDBAPI().loadLikesFromDB(mFeedItem, listener);
    }

    /**
     * 从数据库中加载评论</br>
     */
    private void loadCommentsFromDB() {
        mDatabaseAPI.getCommentAPI()
                .loadCommentsFromDB(mFeedItem.id, new Listeners.SimpleFetchListener<List<Comment>>() {

                    @Override
                    public void onComplete(List<Comment> comments) {
                        comments = filterInvalidComment(comments);
                        comments.removeAll(mFeedItem.comments);
                        mFeedItem.comments.addAll(comments);
                        if (mFeedItem.commentCount == 0) {
                            mFeedItem.commentCount = mFeedItem.comments.size();
                        }
                        sortComments();
                        mCommentView.updateCommentView();
                    }
                });
    }

    private void sortComments() {
        Collections.sort(mFeedItem.comments, mComparator);
    }

    private List<Comment> filterInvalidComment(List<Comment> data){
        List<Comment> comments = new LinkedList<Comment>();
        for (Comment comment : data){
            if(!(comment.status <= 5 && comment.status >= 2)){
                comments.add(comment);
            }
        }
        return comments;
    }

    protected Comparator<Comment> mComparator = new Comparator<Comment>() {

        @Override
        public int compare(Comment lhs, Comment rhs) {
            if (rhs != null && rhs.createTime != null && lhs != null && lhs.createTime != null) {
                return rhs.createTime.compareTo(lhs.createTime);
            }
            return (lhs == null || lhs.createTime == null) ? -1 : 1;
        }
    };

    @Override
    public void loadDataFromServer() {
        loadLikesFromServer();
        loadCommentsFromServer();
    }

    @Override
    public void loadDataFromDB() {
        loadCommentsFromDB();
        loadLikesFromDB();
    }
}
