package com.news.recommend.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NodeEntity
public class Tag {
    @Id
    @GeneratedValue
    private Long id;

    private Integer type;
    private String tagName;

    @Relationship(type = "OWNED_IN")
    private List<News> news;

    @Relationship(direction = Relationship.INCOMING)
    private HashSet<User> users;

    public Tag() {
    }

    public Tag(String tagName,Integer type) {
        this.tagName = tagName;
        this.type = type;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getTagName() {
        return tagName;
    }

    public List<News> getNews() {
        return news;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public HashSet<User> getUsers() {
        return users;
    }

    public void setUsers(HashSet<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        if (this.users == null) {
            this.users = new HashSet<>();
        }
        this.users.add(user);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
