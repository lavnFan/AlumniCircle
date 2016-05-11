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

import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import activeandroid.ActiveAndroid;
import activeandroid.Model;
import activeandroid.query.Delete;
import activeandroid.query.Select;
import activeandroid.util.Log;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.beans.relation.DBRelationOP;
import com.umeng.comm.core.beans.relation.EntityRelationFactory;
import com.umeng.comm.core.db.ctrl.TopicDBAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.utils.CommonUtils;

class TopicDBAPIImpl extends AbsDbAPI<List<Topic>> implements TopicDBAPI {

    @Override
    public void loadTopicsFromDB(final SimpleFetchListener<List<Topic>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
//                List<Topic> topics = new Select().from(Topic.class).orderBy("topic.createTime DESC").execute();
//                CASE WHEN x=w1 THEN r1 WHEN x=w2 THEN r2 ELSE r3 END
//                CASE [expression] WHEN [value] THEN [expression] ELSE [expression] END
                List<Topic> topics = new ArrayList();
                Cursor cursor = null;
                try {
                    String currentUid = CommConfig.getConfig().loginedUser.id;
                    String sql = "select topic.*, CASE WHEN user_topics.user_id='" + currentUid
                            + "' THEN 1 ELSE 0 END isFollowed from topic left join user_topics on " +
                            "topic._id=user_topics.topic_id and user_topics.user_id='" + currentUid +
                            "' order by topic.createTime DESC";

                    Log.d("topic", "sql:" + sql);
                    cursor = ActiveAndroid.getDatabase().rawQuery(sql, null);
                    while (cursor.moveToNext()) {
                        Topic topic = new Topic();
                        topic.id = cursor.getString(cursor.getColumnIndex("_id"));
                        topic.name = cursor.getString(cursor.getColumnIndex("name"));
                        topic.desc = cursor.getString(cursor.getColumnIndex("desc"));
                        topic.createTime = cursor.getString(cursor.getColumnIndex("createTime"));
                        topic.isFocused = cursor.getInt(cursor.getColumnIndex("isFollowed")) == 1;
                        topic.feedCount = cursor.getLong(cursor.getColumnIndex("feedCount"));
                        topic.fansCount = cursor.getLong(cursor.getColumnIndex("fansCount"));
                        topic.icon = cursor.getString(cursor.getColumnIndex("icon"));
//                        Log.d("topic", "topic id:" + cursor.getString(cursor.getColumnIndex("_id"))
//                                + ";isFollowedCul:" + cursor.getInt(cursor.getColumnIndex("isFollowed"))
//                                + ";topicName:" + cursor.getString(cursor.getColumnIndex("name")));
                        topics.add(topic);
                    }
                    Log.d("topic", "result count:" + cursor.getCount());
                } catch (Exception e) {
                    Log.d("topic", "error:" + e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                fillTopicImageItems(topics);
                deliverResult(listener, topics);
            }
        });
    }

    @Override
    public void loadTopicsFromDB(final String uid, final SimpleFetchListener<List<Topic>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<List<Topic>> relationOP = EntityRelationFactory.createUserTopic();
                List<Topic> topics = relationOP.queryById(uid);
                fillTopicImageItems(topics);
                String loginedUid = CommConfig.getConfig().loginedUser.id;
                if(loginedUid.equals(uid)){
                    for (Topic topic : topics) {
                        topic.isFocused = true;
                    }
                }else{
                    DBRelationOP<List<Topic>> db = EntityRelationFactory.createUserTopic();
                    List<Topic> tempData = db.queryById(loginedUid);
                    for (Topic topic : topics){
                        if(tempData.contains(topic)){
                            topic.isFocused = true;
                        }
                    }
                }
                deliverResult(listener, topics);
            }
        });
    }

    @Override
    public void saveTopicsToDB(final List<Topic> topics) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (Topic topic : topics) {
                    topic.saveEntity();
                }
            }
        });
    }

    @Override
    public void saveFollowedTopicsToDB(final String uid, final List<Topic> topics) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (Topic topic : topics) {
                    DBRelationOP<List<Topic>> relationOP = EntityRelationFactory.createUserTopic(
                            new CommUser(uid),
                            topic);
                    relationOP.saveEntity();
                    topic.saveEntity();
                }
            }
        });
    }

    @Override
    public void saveFollowedTopicToDB(final String uid, Topic topic) {
        List<Topic> topics = new ArrayList<Topic>();
        topics.add(topic);
        saveFollowedTopicsToDB(uid, topics);
    }

    private List<ImageItem> selectImagesForTopic(String topicId) {
        return new Select().from(ImageItem.class).where("feedId=?", topicId).execute();
    }

    private void deleteTopicImages(String topicId) {
        new Delete().from(ImageItem.class).where("imageitem.feedId=?", topicId).execute();
    }

    private void fillTopicImageItems(List<Topic> topics) {
        if (CommonUtils.isListEmpty(topics)) {
            return;
        }
        for (Topic topic : topics) {
            List<ImageItem> cacheTopics = selectImagesForTopic(topic.id);
            if (!CommonUtils.isListEmpty(cacheTopics)) {
                topic.imageItems.addAll(cacheTopics);
            }
        }
    }

    @Override
    public void saveRecommendTopicToDB(final List<Topic> topics) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = topics.get(i);
                    DBRelationOP relationOP = EntityRelationFactory.createRecommendTopics(topic.id);
                    relationOP.saveEntity();
                    topic.saveEntity();
                }
            }
        });
    }

    @Override
    public void loadRecommendTopicsFromDB(final SimpleFetchListener<List<Topic>> listener) {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                DBRelationOP<List<Topic>> relationOP = EntityRelationFactory.createRecommendTopics();
                List<Topic> recommendTopic = relationOP.queryById("");

                String loginedUid = CommConfig.getConfig().loginedUser.id;
                if(!TextUtils.isEmpty(loginedUid)){
                    DBRelationOP<List<Topic>> db = EntityRelationFactory.createUserTopic();
                    List<Topic> tempData = db.queryById(loginedUid);
                    for (Topic topic : recommendTopic){
                        if(tempData.contains(topic)){
                            topic.isFocused = true;
                        }
                    }
                }

                fillTopicImageItems(recommendTopic);
                deliverResult(listener, recommendTopic);
            }
        });
    }

    /**
     * 删除全部话题（话题图片、关注话题表、推荐话题表）
     */
    @Override
    public void deleteAllTopics() {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                List<Topic> topics = new Select().from(Topic.class).execute();
                for (int i = 0; i < topics.size(); i++){
                    deleteTopicImages(topics.get(i).id);
                }
                new Delete().from(Topic.class).execute();
            }
        });
        deleteAllRecommendTopics();
        deleteAllFollowedTopic();
    }

    /**
     * 删除单条话题
     * @param topicId
     */
    @Override
    public void deleteTopicFromDB(final String topicId) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                new Delete().from(Topic.class).where("topic._id=?", topicId).execute();
                deleteTopicImages(topicId);
            }
        });
        deleteFollowedTopicByTopicId(topicId);
        deleteRecommendTopics(topicId);
    }


    /**
     * 根据话题id删除关注的话题（用于取消关注）
     * @param topicId
     */
    @Override
    public void deleteFollowedTopicByTopicId(final String topicId) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                CommUser user = CommConfig.getConfig().loginedUser;
                Class<? extends Model> followTopicTable = refectModelClz("com.umeng.comm.core." +
                        "beans.relation.UserTopics");
                if(followTopicTable != null){
                    new Delete().from(followTopicTable).where("user_id=?", user.id).
                            and("topic_id=?", topicId).execute();
                }
            }
        });
    }

    /**
     * 根据用户id删除关注的话题（用于刷新关注列表）
     * @param uid
     */
    @Override
    public void deleteFollowedTopicByUid(final String uid) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                Class<? extends Model> followTopicTable = refectModelClz("com.umeng.comm.core." +
                        "beans.relation.UserTopics");
                if(followTopicTable != null){
                    new Delete().from(followTopicTable).where("user_id=?", uid).execute();
                }
            }
        });
    }

    private void deleteAllFollowedTopic(){
        submit(new DbCommand() {

            @Override
            protected void execute() {
                Class<? extends Model> followTopicTable = refectModelClz("com.umeng.comm.core." +
                        "beans.relation.UserTopics");
                if(followTopicTable != null){
                    new Delete().from(followTopicTable).execute();
                }
            }
        });
    }

    /**
     * 删除推荐话题
     */
    @Override
    public void deleteAllRecommendTopics() {
        submit(new DbCommand() {
            @Override
            protected void execute() {
                Class<? extends Model> recommendTopicTable = refectModelClz("com.umeng.comm.core." +
                        "beans.relation.RecommendTopics");
                if(recommendTopicTable != null){
                    new Delete().from(recommendTopicTable).execute();
                }
            }
        });
    }

    private void deleteRecommendTopics(String topicId){
        DBRelationOP db = EntityRelationFactory.createRecommendTopics();
        db.deleteById(topicId);
    }

    private Class<? extends Model> refectModelClz(String className) {
        try {
            return (Class<? extends Model>) Class.forName(className);
        }catch (Exception e){
            Log.e("topic", e.getMessage().toString());
        }
        return null;
    }
}
