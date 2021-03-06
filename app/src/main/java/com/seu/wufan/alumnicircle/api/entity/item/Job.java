package com.seu.wufan.alumnicircle.api.entity.item;

import java.io.Serializable;

/**
 * @author wufan
 * @date 2016/5/11
 */
public class Job implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * company :
     * job :
     * duration :
     * info :
     */

    private String id;
    private String company;
    private String job;
    private String duration;
    private String info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
