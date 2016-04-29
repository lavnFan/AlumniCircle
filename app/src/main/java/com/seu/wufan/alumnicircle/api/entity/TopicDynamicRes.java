package com.seu.wufan.alumnicircle.api.entity;

import com.seu.wufan.alumnicircle.api.entity.item.TopicDynamicItem;

import java.util.List;

/**
 * @author wufan
 * @date 2016/4/26
 */
public class TopicDynamicRes {
    List<TopicDynamicItem> topicDynamicItemList;

    public List<TopicDynamicItem> getTopicDynamicItemList() {
        return topicDynamicItemList;
    }

    public void setTopicDynamicItemList(List<TopicDynamicItem> topicDynamicItemList) {
        this.topicDynamicItemList = topicDynamicItemList;
    }
}
