package com.seu.wufan.alumnicircle.ui.widget.leancloud;

import android.content.res.Resources;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.seu.wufan.alumnicircle.api.entity.item.User;
import com.seu.wufan.alumnicircle.common.utils.TLog;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.FriendsManager;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.LeanchatUser;
import com.seu.wufan.alumnicircle.ui.widget.leancloud.event.UserCacheUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/21
 */
public class LeancloudProvider implements ThirdPartUserUtils.ThirdPartDataProvider {

    private List<ThirdPartUserUtils.ThirdPartUser> partUsers = new ArrayList<>();

    @Override
    public ThirdPartUserUtils.ThirdPartUser getSelf() {
        return null;
    }

    @Override
    public void getFriend(String userId, final ThirdPartUserUtils.FetchUserCallBack callBack) {
        for (ThirdPartUserUtils.ThirdPartUser user : getPartUsers()) {
            if (user.userId.equals(userId)) {
                callBack.done(Arrays.asList(user), null);
                return;
            }
        }
        callBack.done(null, new Resources.NotFoundException("not found this user"));
    }

    @Override
    public void getFriends(List<String> list, final ThirdPartUserUtils.FetchUserCallBack callBack) {
        List<ThirdPartUserUtils.ThirdPartUser> userList = new ArrayList<ThirdPartUserUtils.ThirdPartUser>();
        for (String userId : list) {
            for (ThirdPartUserUtils.ThirdPartUser user : getPartUsers()) {
//                TLog.i("TAG", user.userId + user.name + user.avatarUrl);
                if (user.userId.equals(userId)) {
//                    TLog.i("TAG", user.name + user.avatarUrl);
                    userList.add(user);
                    break;
                }
            }
        }
        callBack.done(userList, null);
    }

    @Override
    public void getFriends(int skip, int limit, final ThirdPartUserUtils.FetchUserCallBack callBack) {
        int begin = getPartUsers().size() > skip ? skip : getPartUsers().size();
        int end = getPartUsers().size() > skip + limit ? skip + limit : getPartUsers().size();
        callBack.done(getPartUsers().subList(begin, end), null);
    }

    public void setUsers(List<User> users) {
        List<ThirdPartUserUtils.ThirdPartUser> thirdPartUsers = new ArrayList<>();
        for (User user : users) {
            thirdPartUsers.add(getThirdPartUser(user));
        }
        partUsers = thirdPartUsers;
    }

    public void addUsers(List<User> users) {
        for (User user : users) {
            partUsers.add(getThirdPartUser(user));
        }
    }

    public List<ThirdPartUserUtils.ThirdPartUser> getPartUsers() {
        return partUsers;
    }

    private ThirdPartUserUtils.ThirdPartUser getThirdPartUser(User user) {
        return new ThirdPartUserUtils.ThirdPartUser(user.getUser_id(), user.getName(), user.getImage());
    }
}
