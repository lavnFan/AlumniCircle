package com.seu.wufan.alumnicircle.api.entity.item;

import java.io.Serializable;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/15
 */
public class Jobs implements Serializable{

    private static final long serialVersionUID = 1L;

    private List<Job> jobs;

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
