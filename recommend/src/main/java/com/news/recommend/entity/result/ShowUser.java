package com.news.recommend.entity.result;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ShowUser {
    String userName;
    String phoneNumber;
    Long uid;

    public ShowUser() {
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUid() {
        return String.valueOf(uid);
    }

    @Override
    public String toString() {
        return "ShowUser{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", uid=" + uid +
                '}';
    }
}
