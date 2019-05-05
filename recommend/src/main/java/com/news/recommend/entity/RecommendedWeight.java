package com.news.recommend.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class RecommendedWeight {
    @Id
    @GeneratedValue
    private Long id;

    private String uid;
    private Double userM;
    private Double userR;
    private Double userH;

    public RecommendedWeight() {
    }

    public RecommendedWeight(String uid, Double userM, Double userR, Double userH) {
        this.uid = uid;
        this.userM = userM;
        this.userR = userR;
        this.userH = userH;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getUserM() {
        return userM;
    }

    public void setUserM(Double userM) {
        this.userM = userM;
    }

    public Double getUserR() {
        return userR;
    }

    public void setUserR(Double userR) {
        this.userR = userR;
    }

    public Double getUserH() {
        return userH;
    }

    public void setUserH(Double userH) {
        this.userH = userH;
    }
}
