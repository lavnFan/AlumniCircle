package com.seu.wufan.alumnicircle.api.entity.item;

/**
 * @author wufan
 * @date 2016/5/20
 */
public class FriendRequestItem {


    /**
     * user_id :
     * name :
     * image :
     * school :
     * major :
     * add_friend_text :
     */

    private String user_id;
    private String name;
    private String image;
    private String school;
    private String major;
    private String add_friend_text;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getAdd_friend_text() {
        return add_friend_text;
    }

    public void setAdd_friend_text(String add_friend_text) {
        this.add_friend_text = add_friend_text;
    }
}
