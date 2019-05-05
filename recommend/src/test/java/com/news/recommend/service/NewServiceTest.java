package com.news.recommend.service;

import com.news.recommend.entity.News;
import com.news.recommend.entity.Tag;
import com.news.recommend.entity.TagToNew;
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
public class NewServiceTest {

    @Resource
    private NewService newService;

    @Test
    public void readNews() {
        /*
        1103953730769780736 小黄
        1103199163950039040 小红
        1103197065002876928 小明
         */
        News news= newService.ReadNews("1103197065002876928", "8771762");
    }

    @Test
    public void hotNews() {
        Pageable pageable = PageRequest.of(0, 5);
        Map<String,Object> map =  newService.HotNews("2019-03-01", "2019-03-06",pageable);
        Integer total = (Integer) map.get("total");
        Integer page  = (Integer) map.get("page");
        System.out.println(total + "  " + page);
        List<ShowNews> collectNews = (List<ShowNews>) map.get("list");
        for ( ShowNews c: collectNews
        ) {
            System.out.println(c);
        }
    }

    @Test
    public void mostFamiliarNewsList() {
        List<ShowNews> list = newService.MostFamiliarNewsList("8771505", 5);
        for (ShowNews s: list
             ) {
            System.out.println(s);
        }
    }

    @Test
    public void topicNewsRecom() {
    }

    @Test
    public void findNewsByKeyWord() {
        Pageable pageable = PageRequest.of(0, 10);
        Map<String,Object> map =  newService.FindNewsByKeyWord("中国",pageable);
        Integer total = (Integer) map.get("total");
        Integer page  = (Integer) map.get("page");
        System.out.println(total + "  " + page);
        List<ShowNews> collectNews = (List<ShowNews>) map.get("list");
        for ( ShowNews c: collectNews
        ) {
            System.out.println(c);
        }
    }

}