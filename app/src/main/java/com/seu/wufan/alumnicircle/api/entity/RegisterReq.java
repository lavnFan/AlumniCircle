package com.seu.wufan.alumnicircle.api.entity;

/**
 * @author wufan
 * @date 2016/2/22
 */
public class RegisterReq {
    private String phone_num;
    private String password;
    private String enroll_year;
    private String school;
    private String major;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnroll_year() {
        return enroll_year;
    }

    public void setEnroll_year(String enroll_year) {
        this.enroll_year = enroll_year;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public String getPassword() {
        return password;
    }

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }
}
