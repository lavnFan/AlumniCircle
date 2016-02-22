package com.seu.wufan.alumnicircle.model.api;

/**
 * @author wufan
 * @date 2016/2/22
 */
public class UserInfoRes {

    private String is_master;
    private String user_id;
    private String name;
    private String image;
    private String school;
    private String major;
    private String enroll_year;
    private String student_num;
    private String location;

    public void setIs_master(String is_master) {
        this.is_master = is_master;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setEnroll_year(String enroll_year) {
        this.enroll_year = enroll_year;
    }

    public void setStudent_num(String student_num) {
        this.student_num = student_num;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIs_master() {
        return is_master;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }

    public String getEnroll_year() {
        return enroll_year;
    }

    public String getStudent_num() {
        return student_num;
    }

    public String getLocation() {
        return location;
    }
}
