package com.seu.wufan.alumnicircle.api.entity.item;

import java.io.Serializable;

/**
 * @author wufan
 * @date 2016/5/14
 */
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    private String user_id;
    private String name;
    private String image;
    private String gender;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
