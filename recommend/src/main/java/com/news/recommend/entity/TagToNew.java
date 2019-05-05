package com.news.recommend.entity;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "OWNED_IN")
public class TagToNew {
    @Id
    @GeneratedValue
    private Long id;

    /*新闻分类和TF-IDF提取的标签区分*/
    private int type;

    @StartNode
    private Tag tag;

    @EndNode
    private News aNews;

    public TagToNew() {
    }

    public TagToNew(int type) {
        this.type = type;
    }

    public TagToNew(int type, Tag tag, News aNews) {
        this.type = type;
        this.tag = tag;
        this.aNews = aNews;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public News getaNews() {
        return aNews;
    }

    public void setaNews(News aNews) {
        this.aNews = aNews;
    }
}
