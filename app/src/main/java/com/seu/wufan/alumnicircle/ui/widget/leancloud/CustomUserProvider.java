package com.seu.wufan.alumnicircle.ui.widget.leancloud;

import android.content.res.Resources;

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
    private static List<Friend> dataList = new ArrayList<>();
    private static List<Friend> sourceDataList = new ArrayList<Friend>();

    private static List<ThirdPartUserUtils.ThirdPartUser> partUsers = new ArrayList<ThirdPartUserUtils.ThirdPartUser>();

    private static String[] avatarList = new String[]{
            "http://ac-x3o016bx.clouddn.com/CsaX0GuXL7gXWBkaBFXfBWZPlcanClEESzHxSq2T.jpg",
            "http://ac-x3o016bx.clouddn.com/jUOhrGh3CkIaFwvf4ofNfl7YaBjWlmzSs6q8h4cQ.jpg",
            "http://ac-x3o016bx.clouddn.com/FKnGDRxoy5UcZJWCrd1Tf51XkY4dfv6BvXR3TVOP.jpg",
            "http://ac-x3o016bx.clouddn.com/7d6FwrxPGn1Xoym5QE6EU8PLay1FXyHQmO6cQiBw.jpg",
            "http://ac-x3o016bx.clouddn.com/EHVl1ElC7JGmHQOrcDKaMKQDdeVZVzBJqHBDjqjZ.png",
            "http://ac-x3o016bx.clouddn.com/wYerGOiBrWznlFMjp98UyVm1prS8DV1zand1rjLC.jpg",
            "http://ac-x3o016bx.clouddn.com/PhNmVC496BirXdqH0uNfD9rgbp74eT4qBdX7diIl.jpg",
            "http://ac-x3o016bx.clouddn.com/dqfZn3HVCwrNmnCnY4DZQ4ypvdsJN6iMeQHOuKZ2.png",
            "http://ac-x3o016bx.clouddn.com/A907sNcLmnFECwqL7piOZjuhzah9IsYirreUfH8f.png",
            "http://ac-x3o016bx.clouddn.com/gyYyrsnLdwaC7LHTZ538U51jKqKsZpbrteafNew9.png"
    };

    static {
        dataList.add(new Friend("张三"));
        dataList.add(new Friend("李四"));
        dataList.add(new Friend("王五"));
        dataList.add(new Friend("叶修"));
        dataList.add(new Friend("黄少天"));
        dataList.add(new Friend("唐柔"));
        dataList.add(new Friend("苏沐橙"));
        dataList.add(new Friend("包子"));
        dataList.add(new Friend("王杰希"));
        dataList.add(new Friend("叶秋"));
        dataList.add(new Friend("路平"));
        dataList.add(new Friend("苏唐"));
        dataList.add(new Friend("郭有道"));
        dataList.add(new Friend("郭无术"));
        dataList.add(new Friend("楚云秀"));
        dataList.add(new Friend("冷休谈"));
        dataList.add(new Friend("李遥天"));
        dataList.add(new Friend("楚敏"));
        dataList.add(new Friend("孙送招"));
        dataList.add(new Friend("孙哲平"));
        dataList.add(new Friend("周恩来"));
        dataList.add(new Friend("邓小平"));
        dataList.add(new Friend("毛泽东"));
        dataList.add(new Friend("习近平"));
        dataList.add(new Friend("温家宝"));
        dataList.add(new Friend("朱镕基"));
        dataList.add(new Friend("彭德怀"));
        dataList.add(new Friend("范玮琪"));
        dataList.add(new Friend("陈奕迅"));
        dataList.add(new Friend("Lady Gaga"));
        dataList.add(new Friend("Taylor Swift"));
        dataList.add(new Friend("Beyonce"));
        dataList.add(new Friend("Katy Perry"));
        dataList.add(new Friend("Avril"));
        dataList.add(new Friend("One Republic"));
        dataList.add(new Friend("One Direction"));
        dataList.add(new Friend("Pink"));
        dataList.add(new Friend("Maddona"));
        for (int i = 0; i < dataList.size(); i++) {
            Friend friendModel = new Friend();
            friendModel.setName(dataList.get(i).getName());
            //汉字转换成拼音
            String pinyin = CharacterParser.getSelling(dataList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendModel.setLetters(sortString.toUpperCase());
            } else {
                friendModel.setLetters("#");
            }
            sourceDataList.add(friendModel);
        }
        for (int i = 0; i < dataList.size(); i++) {
            sourceDataList.get(i).setName(dataList.get(i).getName());
        }
        dataList = null; //释放资源
        // 根据a-z进行排序源数据
        Collections.sort(sourceDataList, new PinyinComparator());

        for (int i = 0; i < sourceDataList.size(); ++i) {
            partUsers.add(new ThirdPartUserUtils.ThirdPartUser(sourceDataList.get(i).getName(), sourceDataList.get(i).getName(), avatarList[i % 10]));
        }
    }

    @Override
    public ThirdPartUserUtils.ThirdPartUser getSelf() {
        return new ThirdPartUserUtils.ThirdPartUser("daweibayu", "daweibayu",
                "http://ac-x3o016bx.clouddn.com/CsaX0GuXL7gXWBkaBFXfBWZPlcanClEESzHxSq2T.jpg");
    }

    @Override
    public void getFriend(String userId, ThirdPartUserUtils.FetchUserCallBack callBack) {
        for (ThirdPartUserUtils.ThirdPartUser user : partUsers) {
            if (user.userId.equals(userId)) {
                callBack.done(Arrays.asList(user), null);
                return;
            }
        }
        callBack.done(null, new Resources.NotFoundException("not found this user"));
    }

    @Override
    public void getFriends(List<String> list, ThirdPartUserUtils.FetchUserCallBack callBack) {
        List<ThirdPartUserUtils.ThirdPartUser> userList = new ArrayList<ThirdPartUserUtils.ThirdPartUser>();
        for (String userId : list) {
            for (ThirdPartUserUtils.ThirdPartUser user : partUsers) {
                if (user.userId.equals(userId)) {
                    userList.add(user);
                    break;
                }
            }
        }
        callBack.done(userList, null);
    }

    @Override
    public void getFriends(int skip, int limit, ThirdPartUserUtils.FetchUserCallBack callBack) {
        int begin = partUsers.size() > skip ? skip : partUsers.size();
        int end = partUsers.size() > skip + limit ? skip + limit : partUsers.size();
        callBack.done(partUsers.subList(begin, end), null);
    }
}
