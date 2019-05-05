package com.news.recommend.Toneo4j;

import java.util.Set;

public class BNews {
    /*{'news_id': <news_id>,
    'title':<title>,
    'content':<content>,
    'source': <source>,
    'time':<time>,
    'keywords': <keywords>,
    'desc': <desc>,
    'desc': <desc>}*/

    private String Id;
    private String Url;
    private String Content;
    private String Title;
    private String NewsDate;
    private String NewsTime;
    private String Category;
    private Set<String> Tag;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getNewsDate() {
        return NewsDate;
    }

    public void setNewsDate(String newsDate) {
        NewsDate = newsDate;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getNewsTime() {
        return NewsTime;
    }

    public void setNewsTime(String newsTime) {
        NewsTime = newsTime;
    }

    public Set<String> getTag() {
        return Tag;
    }

    public void setTag(Set<String> tag) {
        Tag = tag;
    }

    public BNews() {
    }

    @Override
    public String toString() {
        return "BNews{" +
                "Id='" + Id + '\'' +
                ", Url='" + Url + '\'' +
//                ", Content='" + Content + '\'' +
                ", Title='" + Title + '\'' +
                ", NewsDate='" + NewsDate + '\'' +
                ", Category='" + Category + '\'' +
                ", Tag=" + Tag +
                '}';
    }
}
