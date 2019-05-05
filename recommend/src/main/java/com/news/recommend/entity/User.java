package com.news.recommend.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

@NodeEntity
public class User {
    @Id @GeneratedValue
    private Long id;

    private Long uid;
    private String userName;
    private String passWord;
    private String phoneNumber;
    /*用户最近登陆时间*/
    private String latestLogTime;
    /*用户喜好关键词*/
    private String prefWords;

    /*记录关注的用户*/
    @Relationship(type = "ATTENTION_IN")
    private List<User> users;

    /*关注用户的关系*/
    @Relationship(type = "ATTENTION_IN", direction = Relationship.INCOMING)
    private List<Attention> attentions;

    @Relationship(type = "READ_IN")
    private List<News> news;

    public User() {
    }

    public User(Long uid, String userName, String passWord, String phoneNumber, String latestLogTime) {
        this.uid = uid;
        this.userName = userName;
        this.passWord = passWord;
        this.phoneNumber = phoneNumber;
        this.latestLogTime = latestLogTime;
    }

    public void addAttention(Attention attention) {
        if (this.attentions == null) {
            this.attentions = new ArrayList<>();
        }
        this.attentions.add(attention);
    }


    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", latestLogTime='" + latestLogTime + '\'' +
                ", prefWords='" + prefWords + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return String.valueOf(uid);
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLatestLogTime() {
        return latestLogTime;
    }

    public void setLatestLogTime(String latestLogTime) {
        this.latestLogTime = latestLogTime;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Attention> getAttentions() {
        return attentions;
    }

    public void setAttentions(List<Attention> attentions) {
        this.attentions = attentions;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public String getPrefWords() {
        return prefWords;
    }

    public void setPrefWords(String prefWords) {
        this.prefWords = prefWords;
    }
}
