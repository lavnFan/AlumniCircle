package com.seu.wufan.alumnicircle.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.seu.wufan.alumnicircle.api.entity.item.Edu;
import com.seu.wufan.alumnicircle.api.entity.item.Job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/11
 */
public class UserInfoDetailRes implements Serializable {

    private static final long serialVersionUID = 1L;

    private String gender;
    private String birthday;
    private String city;
    private String profession;
    private String company;
    private String job;
    private String introduction;
    private List<Edu> eduHistory;
    private List<Job> jobHistory;

    public List<Edu> getEduHistory() {
        return eduHistory;
    }

    public void setEduHistory(List<Edu> eduHistory) {
        this.eduHistory = eduHistory;
    }

    public List<Job> getJobHistory() {
        return jobHistory;
    }

    public void setJobHistory(List<Job> jobHistory) {
        this.jobHistory = jobHistory;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.gender);
//        dest.writeString(this.birthday);
//        dest.writeString(this.city);
//        dest.writeString(this.profession);
//        dest.writeString(this.company);
//        dest.writeString(this.job);
//        dest.writeString(this.introduction);
//        dest.writeList(this.eduHistory);
//        dest.writeList(this.jobHistory);
//    }
//
//    public UserInfoDetailRes() {
//    }
//
//    protected UserInfoDetailRes(Parcel in) {
//        this.gender = in.readString();
//        this.birthday = in.readString();
//        this.city = in.readString();
//        this.profession = in.readString();
//        this.company = in.readString();
//        this.job = in.readString();
//        this.introduction = in.readString();
//        this.eduHistory = new ArrayList<Edu>();
//        in.readList(this.eduHistory, Edu.class.getClassLoader());
//        this.jobHistory = new ArrayList<Job>();
//        in.readList(this.jobHistory, Job.class.getClassLoader());
//    }
//
//    public static final Parcelable.Creator<UserInfoDetailRes> CREATOR = new Parcelable.Creator<UserInfoDetailRes>() {
//        @Override
//        public UserInfoDetailRes createFromParcel(Parcel source) {
//            return new UserInfoDetailRes(source);
//        }
//
//        @Override
//        public UserInfoDetailRes[] newArray(int size) {
//            return new UserInfoDetailRes[size];
//        }
//    };
}
