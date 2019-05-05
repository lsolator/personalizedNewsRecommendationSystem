package com.news.recommend.service;

import com.news.recommend.dao.RWeightRepository;
import com.news.recommend.dao.UserRepository;
import com.news.recommend.entity.RecommendedWeight;
import com.news.recommend.entity.User;
import com.news.recommend.entity.result.*;
import com.news.util.IdGen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private RecommendService recommendService;

    @Value("${startDate}")
    private String startDate;

    @Value("${endDate}")
    private String endDate;

    @Resource
    private RWeightRepository rWeightRepository;


    @Test
    public void addUser() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User u = new User(IdGen.get().nextId(),"小黄","123456","77777",sdf.format(new Date()));
        userService.SaveUser(u);
    }

    @Test
    public void addNewsComment() {
    }

    @Test
    public void alertIsCollect() {
        userService.AlertIsCollect("1103197065002876928", "8771956", 1);
    }

    @Test
    public void userCollectNews() {
        Pageable pageable = PageRequest.of(0, 4);
        Map<String,Object> map =  userService.UserReadLogs("1103197065002876928", pageable);
        Integer total = (Integer) map.get("total");
        Integer page  = (Integer) map.get("page");
        System.out.println(total + "  " + page);
        List<CollectNews> collectNews = (List<CollectNews>) map.get("list");
        for ( CollectNews c: collectNews
             ) {
            System.out.println(c);
        }

    }

    @Test
    public void attention() {
        userService.DeleteAttentionUser("1103593100829261824","1103199163950039040");
        /*List<ShowUser> list = userService.ShowAttentionUserList("1103199163950039040");
        for (ShowUser u: list
             ) {
            System.out.println(u);
        }*/
    }

    /*@Test
    public void attentionTopic() {
        //["李克强","习近平","人工智能","中国","知识产权"]
//        userService.AttentionTopic("1103197065002876928","国际新闻");
        List<ShowNews> list = userService.("1103197065002876928", 10);
//        List<TagAndNews> list = userService.TopicRecommend("1103199163950039040",0,10);
        for (ShowNews t: list
             ) {
            System.out.println(t);
        }
    }*/

    /*@Test
    public void test() {
        HashMap<String, RecommendNews> map = recommendService.RecommendNews("1103199163950039040");
        for (String r:map.keySet()
             ) {
            System.out.println(map.get(r));
        }
     }*/

    @Test
    public void tt() {
        RecommendedWeight weight = new RecommendedWeight("1103199163950039040", 4.0, 4.0, 2.0);
        rWeightRepository.save(weight);
    }

}