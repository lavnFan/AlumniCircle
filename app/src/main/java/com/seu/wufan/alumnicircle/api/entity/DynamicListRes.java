package com.seu.wufan.alumnicircle.api.entity;

import com.seu.wufan.alumnicircle.api.entity.item.DynamicItem;

import java.util.List;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class DynamicListRes {
    List<DynamicItem> list;

    public List<DynamicItem> getList() {
        return list;
    }

    public void setList(List<DynamicItem> list) {
        this.list = list;
    }
}
