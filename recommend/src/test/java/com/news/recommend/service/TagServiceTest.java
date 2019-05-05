package com.news.recommend.service;

import com.news.recommend.entity.Tag;
import com.news.recommend.entity.result.ShowNews;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagServiceTest {

    @Resource
    private TagService tagService;

    @Test
    public void getTagList() {
        List<Tag> tags = tagService.getTagList();
        for (Tag t: tags
             ) {
            System.out.println(t);
        }
    }

    @Test
    public void TopicTest() {
        /*Pageable pageable = PageRequest.of(0, 10);
        Map<String,Object> map =  tagService.HotTopicNewsList("2019-03-01","2019-03-07",pageable);
        Integer total = (Integer) map.get("total");
        Integer page  = (Integer) map.get("page");
        System.out.println(total + "  " + page);
        List<ShowNews> collectNews = (List<ShowNews>) map.get("list");
        for ( ShowNews c: collectNews
        ) {
            System.out.println(c);
        }*/
        List<Tag> tags = tagService.NewsHotWord("8771476",2);
        for (Tag t: tags
             ) {
            System.out.println(t);
        }
    }
}