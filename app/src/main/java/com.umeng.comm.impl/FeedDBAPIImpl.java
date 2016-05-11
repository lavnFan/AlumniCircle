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

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.beans.Medal;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.beans.relation.DBRelationOP;
import com.umeng.comm.core.beans.relation.EntityRelationFactory;
import com.umeng.comm.core.db.ctrl.FeedDBAPI;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.utils.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import activeandroid.ActiveAndroid;
import activeandroid.Model;
import activeandroid.query.Delete;
import activeandroid.query.Select;
import activeandroid.query.Update;

/**
 * 对feed在本地进行add、delete、query 操作。
 */
class FeedDBAPIImpl extends AbsDbAPI<List<FeedItem>> implements FeedDBAPI {

//    private int mOffset = 0;
//    private int mFeedCount = 0;

    public static int LOAD_FEED_LIMIT = 50;

    private final static String GLOBAL_TOP_FEED_TOPIC_ID = "000000";

    /**
     * following feed
     *
     * @param listener
     */
    @Override
    public void loadFeedsFromDB(final SimpleFetchListener<List<FeedItem>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
//                initFeedsCount();
                Log.d("database", "loading feeds from DB");
                // 分页加载
//                List<FeedItem> items = new Select().from(FeedItem.class)
//                                .where("followOrder=1")
//                                .orderBy("publishTime DESC")
//                                .limit(LOAD_FEED_LIMIT).offset(mOffset)
//                                .execute();
                List<FeedItem> items = loadFollowedFeed();
//                List<FeedItem> topFeeds = loadTopFeed(GLOBAL_TOP_FEED_TOPIC_ID);
//                items.addAll(0, topFeeds);
                fillItems(items);
                items = getFeedsExcludeTops(items);
                deliverResult(listener, items);
//                updateOffset();
            }
        });
    }

//    private void initFeedsCount() {
//        if (mFeedCount == 0) {
//            mFeedCount = new Select().from(FeedItem.class).count();
//        }
//    }
//
//    private void updateOffset() {
//        if (mOffset + Constants.COUNT <= mFeedCount) {
//            mOffset += Constants.COUNT;
//        } else {
//            mOffset = mFeedCount;
//        }
//    }

//    @Override
//    public void resetOffset() {
//        mOffset = 0;
//    }


    @Override
    public void loadFeedsFromDB(final String uid, final SimpleFetchListener<List<FeedItem>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                List<FeedItem> items = new Select().from(FeedItem.class).orderBy("publishTime DESC").execute();
                fillItems(items);
                List<FeedItem> targetItems = filterFeedItems(items, uid);
                deliverResult(listener, targetItems);
            }
        });
    }

    private List<FeedItem> filterFeedItems(List<FeedItem> response, String uid) {
        List<FeedItem> targetItems = new ArrayList<FeedItem>();
        for (FeedItem feedItem : response) {
            if (feedItem.creator.id.equals(uid)) {
                targetItems.add(feedItem);
            }
        }
        return targetItems;
    }

    /**
     * modify by pei
     * time 2016.1.18
     * recommendFeed
     *
     * @param listener
     */
    @Override
    public void loadRecommendFeedsFromDB(final SimpleFetchListener<List<FeedItem>> listener) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                DBRelationOP<List<FeedItem>> db = EntityRelationFactory.createRecommendFeed();
                List<FeedItem> items = db.queryById("");
                List<FeedItem> topFeeds = loadTopFeed(GLOBAL_TOP_FEED_TOPIC_ID);
                items.addAll(0, topFeeds);
                fillItems(items);
                deliverResult(listener, items);
            }
        });
    }

    /**
     * modify by pei
     * time 2016.1.18
     *
     * @param listener
     */
    @Override
    public void loadFriendsFeedsFromDB(final SimpleFetchListener<List<FeedItem>> listener) {
        List<FeedItem> items = new Select().from(FeedItem.class)
                              .where("isfriends=1").orderBy("publishTime DESC")
                              .limit(LOAD_FEED_LIMIT).offset(0).execute();
        fillItems(items);
        items = getFeedsExcludeTops(items);
        deliverResult(listener, items);
    }

    private List<FeedItem> getFeedsExcludeTops(List<FeedItem> feeds){
        // 由于从缓存中的对象引用是一致的，因此不能直接改变对象的属性，需要clone对象
        List<FeedItem> normalFeeds = new ArrayList();
        for (FeedItem feed : feeds){
            FeedItem normalFeed = feed.clone();
            normalFeed.isTop = 0;
            normalFeeds.add(normalFeed);
        }
        return normalFeeds;
    }

    @Override
    public void loadFavoritesFeed(SimpleFetchListener<List<FeedItem>> listener) {
        List<FeedItem> items = new Select().from(FeedItem.class)
                .where("iscollected=1").orderBy("addTime DESC")
                .limit(LOAD_FEED_LIMIT).offset(0).execute();
        fillItems(items);
        deliverResult(listener, items);
    }

    /**
     * currently no use weibo version
     *
     * @param days
     */
    @Override
    public void loadHotFeeds(final int days, final SimpleFetchListener<List<FeedItem>> listener) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                DBRelationOP<List<FeedItem>> db = null;
                switch (days) {
                    case 1:
                        db = EntityRelationFactory.createHotFeedOne();
                        break;

                    case 3:
                        db = EntityRelationFactory.createHotFeedThree();
                        break;

                    case 7:
                        db = EntityRelationFactory.createHotFeedSeven();
                        break;

                    case 30:
                        db = EntityRelationFactory.createHotFeedThirty();
                        break;

                    default:
                        break;
                }
                List<FeedItem> items = new LinkedList<FeedItem>();
                if (db != null) {
                    List<FeedItem> result = db.queryById("");
                    items.addAll(result);
                }
                List<FeedItem> topFeeds = loadTopFeed(GLOBAL_TOP_FEED_TOPIC_ID);
                items.addAll(0, topFeeds);
                fillItems(items);
                deliverResult(listener, items);
            }
        });
    }

    @Override
    public void loadNearbyFeed(SimpleFetchListener<List<FeedItem>> listener) {
        List<FeedItem> items = new Select().from(FeedItem.class)
                .where("isnearby=1")
                .orderBy("distance").execute();
        fillItems(items);
        deliverResult(listener, items);
    }

    /**
     * save followed feed
     *
     * @param feedItems 需要保存的feed列表
     */
    @Override
    public void saveFeedsToDB(final List<FeedItem> feedItems) {
        if (feedItems == null || feedItems.size() == 0) {
            return;
        }
        for (FeedItem feedItem : feedItems) {
            if (feedItem.sourceFeed != null) {
                saveFeedToDB(feedItem.sourceFeed);// 保存被转发的feed
            }
            // 保存feed
            saveFeedToDB(feedItem);
        }
        updateFollowedFeedTable(feedItems);
    }

    /**
     * update followed feed sequence table
     * 要保证操作是顺序执行，不能异步执行，否则操作表数据混乱
     *
     * @param feedItems
     */
    private void updateFollowedFeedTable(final List<FeedItem> feedItems) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                ActiveAndroid.beginTransaction();
                try {
                    clearFollowedFeed();
                    int len = feedItems.size();
                    for (int i = 0; i < len; i++) {
                        FeedItem feedItem = feedItems.get(i);
                        if (feedItem.isTop == 1) {
                            saveTopFeed(feedItem, GLOBAL_TOP_FEED_TOPIC_ID);
                            Log.d("database", "save top feed:" + feedItem.id);
                        } else {
                            saveFollowedFeed(feedItem.id);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                }
            }
        });
    }

    /**
     * 保存单条feed
     *
     * @param feedItem 需要保存的feed
     */
    public void saveFeedToDB(final FeedItem feedItem) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                // 存储feed本身的信息
//                feedItem.saveEntity();
                // primary key and unique id cannot modify
                String[] exculdeParams = {"mId", "id"};
                String[] whereArgs = {feedItem.id};
//                feedItem.updateEntity();
                feedItem.updateEntityExculde("_id=?", whereArgs, exculdeParams);
                // 存储一些关联信息
                saveRelationship(feedItem);
            }
        });
    }

    /**
     * modify by pei
     * time 2016.1.18
     *
     * @param items 需要保存的推荐feed
     */
    @Override
    public void saveRecommendFeedToDB(final List<FeedItem> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        for (FeedItem feedItem : items) {
            if (feedItem.sourceFeed != null) {
                saveFeedToDB(feedItem.sourceFeed);// 保存被转发的feed
            }
            // 保存feed
            saveFeedToDB(feedItem);
        }
        updateReommendFeedTable(items);
    }

    /**
     * update followed feed sequence table
     * 要保证操作是顺序执行，不能异步执行，否则操作表数据混乱
     *
     * @param feedItems
     */
    private void updateReommendFeedTable(final List<FeedItem> feedItems) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                ActiveAndroid.beginTransaction();
                try {
                    clearRecommendFeed();
                    int len = feedItems.size();
                    for (int i = 0; i < len; i++) {
                        FeedItem feedItem = feedItems.get(i);
                        if (feedItem.isTop == 1) {
                            saveTopFeed(feedItem, GLOBAL_TOP_FEED_TOPIC_ID);
                            Log.d("database", "save top feed:" + feedItem.id);
                        } else {
                            DBRelationOP db = EntityRelationFactory.createRecommendFeed(feedItem.id);
                            db.saveEntity();
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                }
            }
        });
    }

    public void saveHotFeedToDB(final int days, final List<FeedItem> feedItems) {
        for (FeedItem feedItem : feedItems) {
            if (feedItem.sourceFeed != null) {
                saveFeedToDB(feedItem.sourceFeed);// 保存被转发的feed
            }
            // 保存feed
            saveFeedToDB(feedItem);
        }
        updateHotFeedTable(days, feedItems);
    }

    /**
     * update followed feed sequence table
     * 要保证操作是顺序执行，不能异步执行，否则操作表数据混乱
     *
     * @param feedItems
     */
    private void updateHotFeedTable(final int days, final List<FeedItem> feedItems) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                ActiveAndroid.beginTransaction();
                try {
                    clearHotFeed(days);
                    int len = feedItems.size();
                    for (int i = 0; i < len; i++) {
                        FeedItem feedItem = feedItems.get(i);
                        if (feedItem.isTop == 1) {
                            saveTopFeed(feedItem, GLOBAL_TOP_FEED_TOPIC_ID);
                            Log.d("database", "save top feed:" + feedItem.id);
                        } else {
                            DBRelationOP db = null;
                            switch (days) {
                                case 1:
                                    db = EntityRelationFactory.createHotFeedOne(feedItem.id);
                                    break;

                                case 3:
                                    db = EntityRelationFactory.createHotFeedThree(feedItem.id);
                                    break;

                                case 7:
                                    db = EntityRelationFactory.createHotFeedSeven(feedItem.id);
                                    break;

                                case 30:
                                    db = EntityRelationFactory.createHotFeedThirty(feedItem.id);
                                    break;

                                default:
                                    break;
                            }
                            if (db != null) {
                                db.saveEntity();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                }
            }
        });
    }


    /**
     * save top feed
     *
     * @param feedItem
     * @param topId
     */
    private void saveTopFeed(final FeedItem feedItem, final String topId) {
        DBRelationOP<?> relationOP = EntityRelationFactory.createFeedTop(feedItem.id, topId);
        relationOP.saveEntity();
    }

    private List<FeedItem> loadTopFeed(String topId) {
        if (TextUtils.isEmpty(topId)) {
            return null;
        }
        DBRelationOP<List<FeedItem>> relationOP = EntityRelationFactory.createFeedTop();
        List<FeedItem> topFeeds = relationOP.queryById(topId);
        List<FeedItem> topFeedsClone = new ArrayList<FeedItem>();
        if (topFeeds != null) {
            for (FeedItem item : topFeeds) {
                FeedItem itemClone = item.clone();
                itemClone.isTop = 1;
                topFeedsClone.add(itemClone);
            }
        }
        return topFeedsClone;
    }


    private void clearTopFeed(String topicId) {
        try {
            Class<? extends Model> className = (Class<? extends Model>) Class.
                    forName("com.umeng.comm.core.beans.relation.FeedTop");
            new Delete().from(className).where("top_id=?", topicId).execute();
            Log.d("database", "clearTopFeed:" + topicId);
        } catch (Exception e) {
            e.toString();
        }
    }

    /**
     * modify by pei
     * time 2016.1.18
     */
    @Override
    public void clearRecommendFeed() {
        clearTopFeed(GLOBAL_TOP_FEED_TOPIC_ID);
        try {
            Class<? extends Model> className = (Class<? extends Model>) Class.
                    forName("com.umeng.comm.core.beans.relation.RecommendFeed");
            new Delete().from(className).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearHotFeed(final int days) {
        clearTopFeed(GLOBAL_TOP_FEED_TOPIC_ID);
        String table = null;
        switch (days) {
            case 1:
                table = "com.umeng.comm.core.beans.relation.HotFeedOne";
                break;

            case 3:
                table = "com.umeng.comm.core.beans.relation.HotFeedThree";
                break;

            case 7:
                table = "com.umeng.comm.core.beans.relation.HotFeedSeven";
                break;

            case 30:
                table = "com.umeng.comm.core.beans.relation.HotFeedThirty";
                break;

            default:
                break;
        }
        try {
            if (TextUtils.isEmpty(table)) {
                return;
            }
            Class<? extends Model> className = (Class<? extends Model>) Class.forName(table);
            new Delete().from(className).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFollowedFeed() {
//        clearTopFeed(GLOBAL_TOP_FEED_TOPIC_ID);
        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.FollowedFeed");
            new Delete().from(followedFeedClz).execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 待验证
     *
     * @param uid 好友id
     */
    @Override
    public void deleteFriendFeed(final String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        submit(new DbCommand() {
            @Override
            protected void execute() {
                List<FeedItem> items = new Select().from(FeedItem.class)
                        .where("isfriends=1").execute();
                for (FeedItem item : items) {
                    DBRelationOP<CommUser> feedCreator = EntityRelationFactory.createFeedCreator();
                    CommUser user = feedCreator.queryById(item.id);
                    if (uid.equals(user.id)) {
                        new Update(FeedItem.class).set("isfriends=0").where("_id=?", item.id);
                    }
                }
            }
        });
    }

    @Override
    public void deleteFavoritesFeed(final String feedId) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                // 假删除
                new Update(FeedItem.class).set("isCollected=0").where("feedId=?", feedId).execute();
            }
        });
    }

    @Override
    public void queryFeedCount(final String uid, final SimpleFetchListener<Integer> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<?> relationOP = EntityRelationFactory.createFeedCreator();
                deliverResultForCount(listener, relationOP.queryCountById(uid));
            }
        });
    }

    /**
     * 存储feed的关系表,例如评论、@的好友、like等
     *
     * @param feedItem
     */
    private void saveRelationship(FeedItem feedItem) {
        DBRelationOP<CommUser> feedCreator = EntityRelationFactory.createFeedCreator(feedItem,
                feedItem.creator);
        feedCreator.saveEntity();
        saveUserMedals(feedItem.creator.id, feedItem.creator.medals);

        // 存储@的好友
        List<CommUser> friends = new ArrayList<CommUser>(feedItem.atFriends);
        for (CommUser friend : friends) {
            DBRelationOP<?> feedFriends = EntityRelationFactory.createFeedFriends(feedItem, friend);
            feedFriends.saveEntity();
            saveUserMedals(friend.id, friend.medals);
        }

        // 存储feed所属的话题
        List<Topic> topics = new ArrayList<Topic>(feedItem.topics);
        for (Topic topic : topics) {
            DBRelationOP<?> feedTopic = EntityRelationFactory.createFeedTopic(feedItem, topic);
            feedTopic.saveEntity();
        }

        // 存储feed的赞
        List<Like> likes = new ArrayList<Like>(feedItem.likes);
        for (Like like : likes) {
            DBRelationOP<?> feedLike = EntityRelationFactory.createFeedLike(feedItem, like);
            feedLike.saveEntity();
        }

        // 存储feed的评论
        List<Comment> comments = new ArrayList<Comment>(feedItem.comments);
        for (Comment comment : comments) {
            DBRelationOP<?> feedComment = EntityRelationFactory
                    .createFeedComment(feedItem, comment);
            feedComment.saveEntity();
        }
    }

    private void saveUserMedals(String uid, List<Medal> medals){
        DBRelationOP<?> dbRelationOP = EntityRelationFactory.createUserMedals();
        dbRelationOP.deleteById(uid);
        if (medals!=null) {
            for (Medal medal : medals) {
                DBRelationOP db = EntityRelationFactory.createUserMedals(uid, medal.id);
                db.saveEntity();
            }
        }
    }

    /**
     * 填充feed的相关数据</br>
     *
     * @param items
     */
    private void fillItems(List<FeedItem> items) {
        for (final FeedItem item : items) {
            if (!TextUtils.isEmpty(item.sourceFeedId)) {
                FeedItem feedItem = new Select().from(FeedItem.class)
                        .where("_id=?", item.sourceFeedId)
                        .executeSingle();
                fillOneItem(feedItem);// 填充源feed的数据
                item.sourceFeed = feedItem;
            }
            fillOneItem(item);
        }
    }

    /**
     * 填充feed每项数据</br>
     *
     * @param item
     */
    private void fillOneItem(FeedItem item) {
        if (item == null) {
            return;
        }
        DBRelationOP<CommUser> feedCreator = EntityRelationFactory.createFeedCreator();
        DBRelationOP<List<CommUser>> feedFriends = EntityRelationFactory.createFeedFriends();
        DBRelationOP<List<Topic>> feedTopic = EntityRelationFactory.createFeedTopic();
        DBRelationOP<List<Like>> feedLike = EntityRelationFactory.createFeedLike();
        // DBRelationOP<List<Comment>> feedComment =
        // EntityRelationFactory.createFeedComment();

        item.creator = feedCreator.queryById(item.id);
        DBRelationOP<List<Medal>> userMedals = EntityRelationFactory.createUserMedals();
        item.creator.medals = userMedals.queryById(item.creator.id);

        item.atFriends = feedFriends.queryById(item.id);
        for (CommUser friend:item.atFriends){
            DBRelationOP<List<Medal>> medals = EntityRelationFactory.createUserMedals();
            friend.medals = medals.queryById(friend.id);
        }
        item.imageUrls = selectImagesForFeed(item.id);
        item.topics = feedTopic.queryById(item.id);
        item.likes = feedLike.queryById(item.id);
        Log.d("database", "size: " + item.likes.size());
        // item.comments = feedComment.queryById(item.id);
    }

    private List<ImageItem> selectImagesForFeed(String feedId) {
        return new Select().from(ImageItem.class).where("feedId=?", feedId).execute();
    }


    @Override
    public void deleteFeedFromDB(final String feedId) {
        if (TextUtils.isEmpty(feedId)) {
            return;
        }

        submit(new DbCommand() {

            @Override
            protected void execute() {
                // 删除feed本身
                new Delete().from(FeedItem.class).where("_id=?", feedId).execute();
                // 删除跟feed相关的关系表
                DBRelationOP<?> feedCreator = EntityRelationFactory.createFeedCreator();
                feedCreator.deleteById(feedId);
                DBRelationOP<?> feedFriends = EntityRelationFactory.createFeedFriends();
                feedFriends.deleteById(feedId);
                DBRelationOP<?> feedTopic = EntityRelationFactory.createFeedTopic();
                feedTopic.deleteById(feedId);
                // add by pei 2016.1.21
                DBRelationOP<?> feedTop = EntityRelationFactory.createFeedTop();
                feedTop.deleteById(feedId);

                try {
                    Class<? extends Model> feedLikeClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedLike");
                    Class<? extends Model> feedCommentClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedComment");
                    // 移除like相关
                    removeRelativeLike(feedId, feedLikeClass);
                    // 移除Feed相关的Comment
                    removeRelativeComment(feedId, feedCommentClass);
                } catch (Exception e) {
                }

                // 删除图片
                deleteImagesForFeed(feedId);

                deleteSequenceTableById(feedId);
            }
        });
    }

    @Override
    public void deleteAllFeedsFromDB() {
        submit(new DeleteAllFeedItemCmd());
    }

    /**
     * 删除Feed相关的Like、评论、feed-topic、feed-friend等关系记录,评论、赞本书的数据也会被删除
     */
    private class DeleteAllFeedItemCmd extends DbCommand {

        @Override
        protected void execute() {
            try {
                removeFeedRelativeItems();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // 删除Feed本身的数据
            new Delete().from(FeedItem.class).execute();
        }

        private void removeFeedRelativeItems() throws ClassNotFoundException {
            // 获取所有缓存的Feed
            List<FeedItem> cacheFeedItems = new Select().from(FeedItem.class).execute();

            Class<? extends Model> feedLikeClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedLike");
            Class<? extends Model> feedCommentClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedComment");
            Class<? extends Model> feedTopicClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedTopic");
            Class<? extends Model> feedCreatorClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedCreator");
            Class<? extends Model> feedFriendClass = refectModelClz("com.umeng.comm.core.beans.relation.FeedFriends");

            for (FeedItem feedItem : cacheFeedItems) {
                // 移除like相关
                removeRelativeLike(feedItem.id, feedLikeClass);
                // 移除Feed相关的Comment
                removeRelativeComment(feedItem.id, feedCommentClass);
                new Delete().from(feedTopicClass).where("feed_id=?", feedItem.id)
                        .execute();
                new Delete().from(feedCreatorClass).where("feed_id=?", feedItem.id)
                        .execute();
                new Delete().from(feedFriendClass).where("feed_id=?", feedItem.id)
                        .execute();

                DBRelationOP<?> feedTop = EntityRelationFactory.createFeedTop();
                feedTop.deleteById(feedItem.id);

                deleteImagesForFeed(feedItem.id);

                deleteSequenceTableById(feedItem.id);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Model> refectModelClz(String className)
            throws ClassNotFoundException {
        return (Class<? extends Model>) Class.forName(className);
    }

    private void removeRelativeLike(String feedId, Class<? extends Model> feedLikeClass)
            throws ClassNotFoundException {
        List<Like> likes = new Select().from(Like.class).innerJoin(feedLikeClass)
                .on("like._id=feed_like.like_id").where("feed_like.feed_id=?", feedId)
                .execute();
        // 删除feed_like表中的记录
        new Delete().from(feedLikeClass).where("feed_id=?", feedId);

        Class<? extends Model> likeCreatorClz = refectModelClz("com.umeng.comm.core.beans.relation.LikeCreator");
        for (Like like : likes) {
            new Delete().from(likeCreatorClz).where("like_id=?", like.id).execute();
            new Delete().from(Like.class).where("_id=?", like.id).execute();
        }
    }

    private void removeRelativeComment(String feedId, Class<? extends Model> feedCommentClass)
            throws ClassNotFoundException {
        List<Comment> comments = new Select().from(Comment.class).innerJoin(feedCommentClass)
                .on("comment._id=feed_comment.comment_id").where("feed_comment.feed_id=?", feedId)
                .execute();
        // 删除feed_comment表中的记录
        new Delete().from(feedCommentClass).where("feed_id=?", feedId);

        Class<? extends Model> commentCreatorClz = refectModelClz("com.umeng.comm.core.beans.relation.CommentCreator");
        for (Comment comment : comments) {
            new Delete().from(commentCreatorClz).where("comment_id=?", comment.id).execute();
            new Delete().from(Comment.class).where("_id=?", comment.id).execute();
        }
    }

    private void deleteImagesForFeed(String feedId) {
        new Delete().from(ImageItem.class).where("feedId=?", feedId).execute();
    }


    // 以下为排序相关逻辑
    private void saveFollowedFeed(String feedId) {
        DBRelationOP<List<FeedItem>> followedFeed = EntityRelationFactory.createFollowedFeed(feedId);
        followedFeed.saveEntity();
    }

    private List<FeedItem> loadFollowedFeed() {
        DBRelationOP<List<FeedItem>> followedFeed = EntityRelationFactory.createFollowedFeed();
        List<FeedItem> feeds = followedFeed.queryById("");
        return feeds;
    }

    /**
     * 根据feedid，删除顺序表中的记录
     *
     * @param feedId
     */
    private void deleteSequenceTableById(String feedId) {
        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.FollowedFeed");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }

        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.HotFeedOne");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }

        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.HotFeedThree");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }

        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.HotFeedSeven");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }

        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.HotFeedThirty");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }

        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.RecommendFeed");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }

        try {
            Class<? extends Model> followedFeedClz = refectModelClz("com.umeng.comm.core.beans.relation.FeedTop");
            new Delete().from(followedFeedClz).where("feed_id=?", feedId).execute();
        } catch (ClassNotFoundException e) {

        }
    }
}
