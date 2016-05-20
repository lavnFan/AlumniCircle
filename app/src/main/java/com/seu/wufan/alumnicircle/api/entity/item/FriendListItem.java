package com.seu.wufan.alumnicircle.api.entity.item;

/**
 * @author wufan
 * @date 2016/5/20
 */
public class FriendListItem {

    private String user_id;
    private String name;
    private  String image;

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
}
