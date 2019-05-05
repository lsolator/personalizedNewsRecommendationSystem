package com.news.recommend.entity.result;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class Comment {
    Long uid;
    String userName;
    String comment;
    String commentTime;

    public Comment() {
    }

    public String getUid() {
        return String.valueOf(uid);
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentTime() {
        return commentTime;
    }
}
