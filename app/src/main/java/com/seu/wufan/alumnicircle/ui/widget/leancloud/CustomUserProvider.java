package com.seu.wufan.alumnicircle.ui.widget.leancloud;

import android.content.res.Resources;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.seu.wufan.alumnicircle.api.entity.item.ContactsFriendsItem;
import com.seu.wufan.alumnicircle.api.entity.item.Friend;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.CharacterParser;
import com.seu.wufan.alumnicircle.ui.widget.pinyin.PinyinComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by wli on 15/12/4.
 */
public class CustomUserProvider implements ThirdPartUserUtils.ThirdPartDataProvider {

    @Override
    public ThirdPartUserUtils.ThirdPartUser getSelf() {
        return new ThirdPartUserUtils.ThirdPartUser("daweibayu", "daweibayu",
                "http://ac-x3o016bx.clouddn.com/CsaX0GuXL7gXWBkaBFXfBWZPlcanClEESzHxSq2T.jpg");
    }

    @Override
    public void getFriend(String userId, final ThirdPartUserUtils.FetchUserCallBack callBack) {
        if (UserCacheUtils.hasCachedUser(userId)) {
            callBack.done(Arrays.asList(getThirdPartUser(UserCacheUtils.getCachedUser(userId))), null);
        } else {
            UserCacheUtils.fetchUsers(Arrays.asList(userId), new UserCacheUtils.CacheUserCallback() {
                @Override
                public void done(List<LeanchatUser> userList, Exception e) {
                    callBack.done(getThirdPartUsers(userList), e);
                }
            });
        }
    }

    @Override
    public void getFriends(List<String> list, final ThirdPartUserUtils.FetchUserCallBack callBack) {
        UserCacheUtils.fetchUsers(list, new UserCacheUtils.CacheUserCallback() {
            @Override
            public void done(List<LeanchatUser> userList, Exception e) {
                callBack.done(getThirdPartUsers(userList), e);
            }
        });
    }

    @Override
    public void getFriends(int skip, int limit, final ThirdPartUserUtils.FetchUserCallBack callBack) {
        FriendsManager.fetchFriends(false, new FindCallback<LeanchatUser>() {
            @Override
            public void done(List<LeanchatUser> list, AVException e) {
                callBack.done(getThirdPartUsers(list), e);
            }
        });
    }

    private static ThirdPartUserUtils.ThirdPartUser getThirdPartUser(LeanchatUser leanchatUser) {
        return new ThirdPartUserUtils.ThirdPartUser(leanchatUser.getObjectId(), leanchatUser.getUsername(), leanchatUser.getAvatarUrl());
    }

    public static List<ThirdPartUserUtils.ThirdPartUser> getThirdPartUsers(List<LeanchatUser> leanchatUsers) {
        List<ThirdPartUserUtils.ThirdPartUser> thirdPartUsers = new ArrayList<>();
        for (LeanchatUser user : leanchatUsers) {
            thirdPartUsers.add(getThirdPartUser(user));
        }
        return thirdPartUsers;
    }
}
