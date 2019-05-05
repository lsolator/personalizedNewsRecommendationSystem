package com.news.recommend.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NodeEntity
public class News {

    @Id
    @GeneratedValue
    private Long id;

    private String nid;
    private String title;
    private String content;

    @DateString("yyyy-MM-dd")
    private Date date;
    private String newsTime;
    private Integer readNum;

    /*分类*/
    /*关键字 标签*/
    @Relationship(type = "OWNED_IN",direction = Relationship.INCOMING)
    private List<TagToNew> tagToNews;


    @Relationship(type = "READ_IN", direction = Relationship.INCOMING)
    private List<NewsLog> newsLogs;

    public News() {
    }

    public News(String nid, String title, String content, Date date, String newsTime, Integer readNum) {
        this.nid = nid;
        this.title = title;
        this.content = content;
        this.date = date;
        this.newsTime = newsTime;
        this.readNum = readNum;
    }

    /*增加标签->到新闻的关系*/
    public void addTagToNew(TagToNew tagToNew) {
        if (this.tagToNews == null) {
            this.tagToNews = new ArrayList<>();
        }
        this.tagToNews.add(tagToNew);
    }

    /*增加用户->到新闻的关系*/
    public void addNewsLog(NewsLog newsLog) {
        if (this.newsLogs == null) {
            this.newsLogs = new ArrayList<>();
        }
        this.newsLogs.add(newsLog);
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", nid='" + nid + '\'' +
                ", title='" + title + '\'' +
                ", readNum=" + readNum +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public List<TagToNew> getTagToNews() {
        return tagToNews;
    }

    public void setTagToNews(List<TagToNew> tagToNews) {
        this.tagToNews = tagToNews;
    }

    public List<NewsLog> getNewsLogs() {
        return newsLogs;
    }

    public void setNewsLogs(List<NewsLog> newsLogs) {
        this.newsLogs = newsLogs;
    }

    public String getDateTime() {
        StringBuilder stringBuilder = new StringBuilder(this.date.toString());
        stringBuilder.append(" ");
        stringBuilder.append(this.getNewsTime());
        return stringBuilder.toString();
    }
}
