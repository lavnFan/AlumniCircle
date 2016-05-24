package com.seu.wufan.alumnicircle.api.entity.item;

/**
 * @author wufan
 * @date 2016/5/22
 */
public class SearchFriendItem {


    /**
     * is_master :
     * user_id :
     * name :
     * image :
     * user_school :
     */

    private String is_master;
    private String user_id;
    private String name;
    private String image;
    private String user_school;

    public String getIs_master() {
        return is_master;
    }

    public void setIs_master(String is_master) {
        this.is_master = is_master;
    }

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

    public String getUser_school() {
        return user_school;
    }

    public void setUser_school(String user_school) {
        this.user_school = user_school;
    }
}
