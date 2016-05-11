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

package com.umeng.comm.impl;

import android.text.TextUtils;

import activeandroid.query.Delete;
import activeandroid.query.Select;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.relation.DBRelationOP;
import com.umeng.comm.core.beans.relation.EntityRelationFactory;
import com.umeng.comm.core.db.ctrl.CommentDBAPI;

import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论的数据库相关操作
 */
class CommentDBAPIImpl extends AbsDbAPI<List<Comment>> implements CommentDBAPI {

    @Override
    public void loadCommentsFromDB(final String feedId,
            final SimpleFetchListener<List<Comment>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<List<Comment>> feedComment = EntityRelationFactory.createFeedComment();
                List<Comment> comments = feedComment.queryById(feedId);
                Log.d("load","loadfromcomment1 "+comments.size()+" "+feedId);
                for(Comment comment:comments){
                    Log.d("load","loadfromcomment2");
                    List<ImageItem> imageItemArrayList = selectImagesForComment(comment.id);
                    comment.imageUrls=imageItemArrayList;
                    if(comment.imageUrls.size()>0){
                        Log.d("load","size="+comment.imageUrls.size()+"  "+comment.imageUrls.get(0).toString());
                    }
                    if(!TextUtils.isEmpty(comment.replyCommentId)){
                        comment.childComment = loadChildComment(comment.replyCommentId);
                    }
                }
                deliverResult(listener, comments);
            }
        });
    }

    private Comment loadChildComment(String commentId){
        Comment comment = new Select().from(Comment.class).where("_id=?",commentId).executeSingle();
        DBRelationOP<CommUser> commentCreatorOP = EntityRelationFactory.createCommentCreator();
        CommUser user = commentCreatorOP.queryById(commentId);
        if (user != null) {
            comment.creator = user;
        }
        return comment;
    }

    @Override
    public void saveCommentsToDB(final FeedItem feedItem) {
        final List<Comment> comments = new ArrayList<Comment>(feedItem.comments);
        if (comments == null || comments.size() == 0) {
            return;
        }

        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (Comment comment : comments) {
                    comment.saveEntity();
                    DBRelationOP<?> feedComment = EntityRelationFactory.createFeedComment(feedItem,
                            comment);
                    feedComment.saveEntity();
                }
            }
        });

    }

    @Override
    public void deleteCommentsFromDB(final String commentId) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                // 这里需要删除评论本身
                new Delete().from(Comment.class).where("_id=?", commentId).execute();
                // 删除评论跟feed的关系表
                DBRelationOP<?> feedComment = EntityRelationFactory.createFeedComment();
                feedComment.deleteById(commentId);
                deleteImagesForComment(commentId);
            }
        });
    }

    private List<ImageItem> selectImagesForComment(String commentId) {
        return new Select().from(ImageItem.class).where("commentId=?", commentId).execute();
    }

    private void deleteImagesForComment(String commentId) {
        new Delete().from(ImageItem.class).where("commentId=?", commentId).execute();
    }

    @Override
    public void loadCommentWithId(String commentId, SimpleFetchListener<Comment> listener) {
        Comment comment = new Select().from(Comment.class).where("_id=?",commentId).executeSingle();
        listener.onComplete(comment);
    }
}
