package com.news.recommend.entity.result;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ShowNews {
    String title;
    Long readNum;
    String date;
    String newsTime;
    String nid;
    String content;

    public ShowNews() {
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

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ShowNews{" +
                "title='" + title + '\'' +
                ", readNum='" + readNum + '\'' +
                ", date='" + date + '\'' +
                ", newsTime='" + newsTime + '\'' +
                ", nid='" + nid + '\'' +
                '}';
    }
}
