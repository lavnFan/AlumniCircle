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
import android.util.Log;

import activeandroid.query.Select;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Medal;
import com.umeng.comm.core.beans.relation.DBRelationOP;
import com.umeng.comm.core.beans.relation.EntityRelationFactory;
import com.umeng.comm.core.db.ctrl.UserDBAPI;
import com.umeng.comm.core.listeners.Listeners;

import java.util.ArrayList;
import java.util.List;

class UserDBAPIImpl extends AbsDbAPI<CommUser> implements UserDBAPI {

    @Override
    public void saveUserInfoToDB(final CommUser user) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                if (user != null) {
                    user.saveEntity();
                }
                saveRelationship(user);
            }
        });
    }

    @Override
    public void deleteUserFromDB(final CommUser user) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                if (user != null) {
                    user.delete();

                    DBRelationOP<?> dbRelationOP = EntityRelationFactory.createUserMedals();
                    dbRelationOP.deleteById(user.id);
                }
            }
        });
    }

    public void saveRelationship(CommUser user){
        if(user.medals == null || TextUtils.isEmpty(user.id)){
            return;
        }

        DBRelationOP<?> dbRelationOP = EntityRelationFactory.createUserMedals();
        dbRelationOP.deleteById(user.id);

        List<Medal> medals = new ArrayList<Medal>(user.medals);
        int size = medals.size();
        for (int i = 0; i < size; i++){
            DBRelationOP<?> relationOP = EntityRelationFactory.createUserMedals(user.id, medals.get(i).id);
            relationOP.saveEntity();
        }
    }

    @Override
    public void loadUserFromDB(final String uId, final Listeners.SimpleFetchListener<CommUser> listener) {
        submit(new DbCommand() {

            @Override
            protected void execute() {
                CommUser tempUser = new Select().from(CommUser.class).where("_id=?", uId).executeSingle();
                if(tempUser != null){
                    DBRelationOP<List<Medal>> userMedals = EntityRelationFactory.createUserMedals();
                    tempUser.medals = userMedals.queryById(tempUser.id);
                }

                if(listener != null){
                    deliverResult(listener, tempUser);
                }
            }
        });

    }
}
