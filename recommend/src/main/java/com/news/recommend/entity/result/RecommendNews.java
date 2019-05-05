package com.news.recommend.entity.result;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Objects;

@QueryResult
public class RecommendNews{
    String title;
    Long readNum;
    String date;
    String newsTime;
    String nid;
    String recommendType;
    String des;

    public RecommendNews() {
    }

    public String getTitle() {
        return title;
    }

    public Long getReadNum() {
        return readNum;
    }

    public String getDate() {
        return date;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public String getNid() {
        return nid;
    }


    public String getRecommendType() {
        return recommendType;
    }

    public String getDes() {
        return des;
    }

    public RecommendNews(String title, Long readNum,String date, String newsTime, String nid, String recommendType, String des) {
        this.readNum = readNum;
        this.title = title;
        this.date = date;
        this.newsTime = newsTime;
        this.nid = nid;
        this.recommendType = recommendType;
        this.des = des;
    }

    public void setRecommendType(String recommendType) {
        this.recommendType = recommendType;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendNews that = (RecommendNews) o;
        return nid.equals(that.nid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nid);
    }

    @Override
    public String toString() {
        return "RecommendNews{" +
                "title='" + title + '\'' +
                ", readNum=" + readNum +
                ", date='" + date + '\'' +
                ", newsTime='" + newsTime + '\'' +
                ", nid='" + nid + '\'' +
                ", recommendType='" + recommendType + '\'' +
                ", des='" + des + '\'' +
                '}';
    }
}
