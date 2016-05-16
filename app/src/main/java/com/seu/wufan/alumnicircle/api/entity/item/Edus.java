package com.seu.wufan.alumnicircle.api.entity.item;

import java.io.Serializable;
import java.util.List;

/**
 * @author wufan
 * @date 2016/5/16
 */
public class Edus implements Serializable{

    private static final long serialVersionUID = 1L;

    private List<Edu> edus;

    public List<Edu> getEdus() {
        return edus;
    }

    public void setEdus(List<Edu> edus) {
        this.edus = edus;
    }
}
