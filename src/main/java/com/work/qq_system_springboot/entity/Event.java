package com.work.qq_system_springboot.entity;

public class Event {
    //主题：点赞、评论、回复
    private String topic;
    private int userId;
    private int entityId;
    private int entityType;
    private int targetId;
    private String comment;

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getTargetId() {
        return targetId;
    }

    public Event setTargetId(int targetId) {
        this.targetId = targetId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Event setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
