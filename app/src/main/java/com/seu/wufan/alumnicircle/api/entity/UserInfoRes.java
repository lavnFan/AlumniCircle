package com.seu.wufan.alumnicircle.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author wufan
 * @date 2016/2/22
 */
public class UserInfoRes implements Serializable{

    private static final long serialVersionUID = 1L;

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

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.is_master);
//        dest.writeString(this.user_id);
//        dest.writeString(this.name);
//        dest.writeString(this.image);
//        dest.writeString(this.school);
//        dest.writeString(this.major);
//        dest.writeString(this.enroll_year);
//        dest.writeString(this.student_num);
//        dest.writeString(this.location);
//    }
//
//    public UserInfoRes() {
//    }
//
//    protected UserInfoRes(Parcel in) {
//        this.is_master = in.readString();
//        this.user_id = in.readString();
//        this.name = in.readString();
//        this.image = in.readString();
//        this.school = in.readString();
//        this.major = in.readString();
//        this.enroll_year = in.readString();
//        this.student_num = in.readString();
//        this.location = in.readString();
//    }
//
//    public static final Parcelable.Creator<UserInfoRes> CREATOR = new Parcelable.Creator<UserInfoRes>() {
//        @Override
//        public UserInfoRes createFromParcel(Parcel source) {
//            return new UserInfoRes(source);
//        }
//
//        @Override
//        public UserInfoRes[] newArray(int size) {
//            return new UserInfoRes[size];
//        }
//    };
}
