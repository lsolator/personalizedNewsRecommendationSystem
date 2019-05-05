package com.news.recommend.entity.result;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TagAndNews {
    String title;
    Long readNum;
    String date;
    String newsTime;
    String nid;
    String tagName;

    public TagAndNews() {
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

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        return "TagAndNews{" +
                "title='" + title + '\'' +
                ", readNum=" + readNum +
                ", date='" + date + '\'' +
                ", newsTime='" + newsTime + '\'' +
                ", nid='" + nid + '\'' +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
