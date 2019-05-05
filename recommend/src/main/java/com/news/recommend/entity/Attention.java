package com.news.recommend.entity;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "ATTENTION_IN")
public class Attention {
    @Id
    @GeneratedValue
    private Long id;

    private String attentionDate;

    @StartNode
    private User userSelf;
    @EndNode
    private User userOther;

    public Attention() {
    }

    public Attention(String attentionDate, User userSelf, User userOther) {
        this.attentionDate = attentionDate;
        this.userSelf = userSelf;
        this.userOther = userOther;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttentionDate() {
        return attentionDate;
    }

    public void setAttentionDate(String attentionDate) {
        this.attentionDate = attentionDate;
    }

    public User getUserSelf() {
        return userSelf;
    }

    public void setUserSelf(User userSelf) {
        this.userSelf = userSelf;
    }

    public User getUserOther() {
        return userOther;
    }

    public void setUserOther(User userOther) {
        this.userOther = userOther;
    }
}
