package com.news.recommend.entity;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "READ_IN")
public class NewsLog {
    @Id
    @GeneratedValue
    private Long id;
    private String readDate;

    //0没有收藏 1收藏了
    private Integer isCollect;
    private String collectTime;

    private String comment;
    private String commentTime;

    @StartNode
    private User user;

    @EndNode
    private News news;


    public NewsLog() {
    }

    public NewsLog(String readDate, News news, User user) {
        this.readDate = readDate;
        this.news = news;
        this.user = user;
    }

    public String getReadDate() {
        return readDate;
    }

    public News getNews() {
        return news;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReadDate(String readDate) {
        this.readDate = readDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Integer getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(Integer isCollect) {
        this.isCollect = isCollect;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
