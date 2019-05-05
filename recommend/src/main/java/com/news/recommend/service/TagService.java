package com.news.recommend.service;

import com.news.recommend.dao.TagRepository;
import com.news.recommend.entity.Tag;
import com.news.recommend.entity.result.ShowNews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /*新建标签保存*/
    public void saveTag(Tag tag) {
        tagRepository.save(tag);
    }

    public List<Tag> getTagList() {
        return tagRepository.findAllTag();
    }

    /**
     * 主题新闻  分页
     * @param tagName
     * @param pageable
     * @return
     */
    public Map<String, Object> TopicNewsList(String tagName,Integer type, Pageable pageable) {
        HashMap<String, Object> map = new HashMap<>(3);
        Page<ShowNews> newsPage = tagRepository.topicNewsList(tagName,type,pageable);
        map.put("total", newsPage.getTotalPages());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    /**
     * 话题 热点词 新闻
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    public Map<String, Object> HotTopicNewsList(String startDate,String endDate, Pageable pageable) {
        HashMap<String, Object> map = new HashMap<>(3);
        Page<ShowNews> newsPage = tagRepository.hotTopicNewsList(startDate,endDate,pageable);
        map.put("total", newsPage.getTotalPages());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    /**
     * 话题页  热点词
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Tag> HotTopicList(String startDate,String endDate) {
        return tagRepository.hotTopicList(startDate, endDate);
    }

    /**
     * 新闻的类型  （主题 和话题）
     * @param nid
     * @param type
     * @return
     */
    public List<Tag> NewsHotWord(String nid,Integer type) {
        return tagRepository.newsHotWord(nid, type);
    }

}
