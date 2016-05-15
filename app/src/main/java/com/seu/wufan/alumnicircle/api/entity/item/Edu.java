package com.seu.wufan.alumnicircle.api.entity.item;

import java.io.Serializable;

/**
 * @author wufan
 * @date 2016/5/11
 */
public class Edu implements Serializable{

        private static final long serialVersionUID = 1L;
    /**
     * school :
     * major :
     * degree :
     * duration :
     * info :
     */

    private String id;
    private String school;
    private String major;
    private String degree;
    private String duration;
    private String info;

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
