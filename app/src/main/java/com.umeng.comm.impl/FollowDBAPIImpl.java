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

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.relation.DBRelationOP;
import com.umeng.comm.core.beans.relation.EntityRelationFactory;
import com.umeng.comm.core.db.ctrl.FollowDBAPI;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;

import java.util.ArrayList;
import java.util.List;

class FollowDBAPIImpl extends AbsDbAPI<List<CommUser>> implements FollowDBAPI {

    @Override
    public void loadFollowedUsersFromDB(final String uid,
            final SimpleFetchListener<List<CommUser>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<List<CommUser>> relation = EntityRelationFactory.createUserFollow();
                List<CommUser> follows = relation.queryById(uid);
                deliverResult(listener, follows);
            }
        });
    }

    @Override
    public void follow(final CommUser user) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                List<CommUser> users = new ArrayList<CommUser>();
                users.add(user);
                follow(users);
            }
        });
    }

    @Override
    public void unfollow(final CommUser user) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<List<CommUser>> relation = EntityRelationFactory.createUserFollow();
                relation.deleteById(user.id);
                updateFeedStatus(user.id, false);
            }
        });
    }

    @Override
    public void isFollowed(final String uid, final SimpleFetchListener<List<CommUser>> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<List<CommUser>> userFollow = EntityRelationFactory.createUserFollow();
                List<CommUser> result = userFollow.queryById(uid);
                deliverResult(listener, result);
            }
        });
    }

    @Override
    public void follow(final List<CommUser> users) {
        final List<CommUser> tempList = new ArrayList<CommUser>(users);
        submit(new DbCommand() {

            @Override
            protected void execute() {
                for (CommUser user : tempList) {
                    user.saveEntity();
                    String uId = CommConfig.getConfig().loginedUser.id;
                    DBRelationOP<List<CommUser>> relation = EntityRelationFactory
                            .createUserFollow(uId, user);
                    relation.saveEntity();
                    updateFeedStatus(user.id, true);
                }
            }
        });
    }

    @Override
    public void queryFollowCount(final String uid, final SimpleFetchListener<Integer> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                DBRelationOP<?> relationOP = EntityRelationFactory.createUserFollow();
                deliverResultForCount(listener, relationOP.queryCountById(uid));
            }
        });
    }

    private void updateFeedStatus(String uId, boolean isFriend){
        DBRelationOP<List<FeedItem>> userFeedTable = EntityRelationFactory.createUserFeed();
        List<FeedItem> temp = userFeedTable.queryById(uId);
        if(temp != null && !temp.isEmpty()){
            for (FeedItem item: temp){
                item.isFriends = isFriend;
                String[] id = {item.id};
                String[] params = {"isFriends"};
                item.updateEntity("_id=?", id, params);
            }
        }
    }

}
