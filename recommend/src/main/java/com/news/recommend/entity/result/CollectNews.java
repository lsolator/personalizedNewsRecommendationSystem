package com.news.recommend.entity.result;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class CollectNews{
    String title;
    String collectTime;
    String nid;
    String date;

    public CollectNews() {
    }

    public String getTitle() {
        return title;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public String getNid() {
        return nid;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "CollectNews{" +
                "date='" + date + '\'' +
                ", collectTime='" + collectTime + '\'' +
                ", title='" + title + '\'' +
                ", nid='" + nid + '\'' +
                '}';
    }

}
