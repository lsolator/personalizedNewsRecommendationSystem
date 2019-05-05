package com.news.recommend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.news.recommend.dao.NewsRepository;
import com.news.recommend.dao.UserRepository;
import com.news.recommend.entity.PrefWord;
import com.news.recommend.entity.RecommendedWeight;
import com.news.recommend.entity.User;
import com.news.recommend.entity.result.RecommendNews;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.entity.result.TagAndNews;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class RecommendService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private NewsRepository newsRepository;

    @Value("${startDate}")
    private String startDate;

    @Value("${endDate}")
    private String endDate;

    @Value("${recommendNum}")
    private String recommendNum;

    @Value("${userM}")
    private String userM;

    @Value("${userR}")
    private String userR;

    @Value("${userH}")
    private String userH;

    public HashMap<String, RecommendNews> RecommendNews(String uid, RecommendedWeight weight) {
        Integer num = Integer.valueOf(recommendNum);
        HashMap<String, RecommendNews> map = new HashMap<>(Integer.valueOf(recommendNum));

        Double Mnum = num * controlRweight(weight.getUserM(), "userM") * 0.5;
        //关注的主题
        List<TagAndNews> topic = RecommendByTopic(uid, 0,Mnum.intValue());
        List<RecommendNews> topicNews = tagNews2RecommendNews(topic, "userM");
        if (topicNews != null ) {
            for (RecommendNews r: topicNews
            ) {
                map.put(r.getNid(), r);
            }
        }
        System.out.println("-----------------------");
        //根据用户自身属性推荐
        //偏好词
        List<RecommendNews> prefWord = RecommendByPrefWord(uid, Mnum.intValue());
        if (prefWord != null) {
            for (RecommendNews r: prefWord
            ) {
                r.setRecommendType("userM");
                r.setDes("根据你的阅读习惯推荐");
                map.put(r.getNid(), r);
                //
            }
        }
        System.out.println("-----------------------");

        Double Rnum = num * controlRweight(weight.getUserM(), "userR") * 0.5;
        //根据用户的关系推荐
        //关注的用户看过
        List<RecommendNews> userLook = RecommendByUserLook(uid, Rnum.intValue());
        if (userLook != null ) {
            for (RecommendNews r: userLook
            ) {
                String type = "userR";
                if (map.containsKey(r.getNid())) {
                    type =  map.get(r.getNid()).getRecommendType()+"-userR";
                }
                r.setDes("你关注的用户也看过");
                r.setRecommendType(type);
                map.put(r.getNid(), r);
                //
            }
        }
        System.out.println("-----------------------");
        //具有相同习惯的用户也看过
        List<RecommendNews> newslog = RecommendBySimilarNewsLog(uid, Rnum.intValue());
        if (newslog != null ) {
            for (RecommendNews r: newslog
            ) {
                String type = "userR";
                if (map.containsKey(r.getNid())) {
                    type = map.get(r.getNid()).getRecommendType()+"-userR";
                }
                r.setDes("相同阅读习惯的用户推荐");
                r.setRecommendType(type);
                map.put(r.getNid(), r);
                //
            }
        }
        System.out.println("-----------------------");

        Double Hnum = num * controlRweight(weight.getUserM(), "userH");
        //热点新闻
        List<ShowNews> hotNews = RecommendByHotNews(0, Hnum.intValue(),uid);
        List<RecommendNews> list = shownews2RecommendNews(hotNews);
        for (RecommendNews r: list
             ) {
            if (map.containsKey(r.getNid())) {
                RecommendNews rr = map.get(r.getNid());
                String type = rr.getRecommendType()+"-userH";
                r.setRecommendType(type);
                r.setDes(rr.getDes());
                map.put(r.getNid(), r);
            } else {
                r.setRecommendType("userH");
                r.setDes("热点新闻推荐");
                map.put(r.getNid(), r);
            }
            //
        }
        System.out.println("-----------------------");
        return map;
    }

    //用户偏好词推荐
    public List<RecommendNews> RecommendByPrefWord(String uid, Integer num) {
        Optional<User> optionalUser = userRepository.findByUid(Long.valueOf(uid));
        if (optionalUser.isPresent() && optionalUser.get().getPrefWords() != null) {
            User user = optionalUser.get();
            Gson gson = new Gson();
            Type setType = new TypeToken<HashSet<PrefWord>>() {
            }.getType();
            HashSet<PrefWord> hashSet = gson.fromJson(user.getPrefWords(), setType);
            Set<String> set = new HashSet<>();
            for (PrefWord p : hashSet
            ) {
                set.add(p.getWord());
            }
            //得到数组
            String[] arr = new String[set.size()];
            set.toArray(arr);
//            String[] arr = new String[]{"李克强","习近平","人工智能","中国","知识产权"};
            List<RecommendNews> list = userRepository.recommendByPrefWord(Long.valueOf(uid), arr, num);
            return list;
        }
        return null;
    }

    //根据关注的话题推荐
    public List<TagAndNews> RecommendByTopic(String uid, Integer start,Integer num) {
        List<TagAndNews> news = userRepository.topicRecommend(Long.valueOf(uid), start, num);
        if (news != null && news.size() != 0) {
            return news;
        } else return null;
    }


    //关注的用户最近看过
    public List<RecommendNews> RecommendByUserLook(String uid, Integer num) {
        List<RecommendNews> news = userRepository.recommendByAttentionUserLook(Long.valueOf(uid), num);
        if (news != null && news.size() != 0) {
            return news;
        } else return null;
    }

    //相同阅读习惯的用户阅读的新闻
    public List<RecommendNews> RecommendBySimilarNewsLog(String uid, Integer num) {
        List<RecommendNews> news = userRepository.recommendBySimilarNewsLog(Long.valueOf(uid), num);
        if (news != null && news.size() != 0) {
            return news;
        } else return null;
    }

    //图推荐算法
    public List<RecommendNews> RecommendMapFunction(String uid, Integer num) {
        List<RecommendNews> news = userRepository.recommendMapFunction(Long.valueOf(uid), num);
        if (news != null && news.size() != 0) {
            return news;
        } else return null;
    }


    //热门新闻
    public List<ShowNews> RecommendByHotNews(Integer page,Integer num,String uid) {
        Pageable pageable = PageRequest.of(page, num);
        Page<ShowNews> news = newsRepository.HotNewsRecommend(startDate, endDate, pageable,Long.valueOf(uid));
        return news.getContent();
    }

    //推荐跟你有相同关注的主题下的其他主题的新闻
    public List<RecommendNews> RecommendBySimilarTag(String uid, Integer num) {
        List<RecommendNews> news = userRepository.recommendBySimilarTag(Long.valueOf(uid), num);
        if (news != null && news.size() != 0) {
            return news;
        } else return null;
    }

    //返回处理过的新闻列表
    private List<RecommendNews> shownews2RecommendNews(List<ShowNews> list) {
        List<RecommendNews> list1 = new ArrayList<>();
        for (ShowNews r: list
             ) {
            RecommendNews recommendNews = new RecommendNews(r.getTitle(), r.getReadNum(), r.getDate(), r.getNewsTime(), r.getNid(), "", "");
            list1.add(recommendNews);
        }
        return list1;
    }

    private List<RecommendNews> tagNews2RecommendNews(List<TagAndNews> list, String recommendType) {
        if (list==null||list.size()==0) return null;
        List<RecommendNews> list1 = new ArrayList<>();
        for (TagAndNews r: list
        ) {
            String dec = "根据你关注的" + r.getTagName() + "推荐";
            RecommendNews news = new RecommendNews(r.getTitle(), r.getReadNum(), r.getDate(), r.getNewsTime(), r.getNid(), recommendType, dec);
            list1.add(news);
        }
        return list1;
    }

    //推荐比重控制
    private Double controlRweight(Double d,String type) {
        if ("userN".equals(type) || "userR".equals(type)) {
            if (d > 0.6) {
                return 0.6;
            } else if (d < 0.2) {
                return 0.2;
            } else {
                return d;
            }
        } else {
            if (d > 0.4) {
                return 0.4;
            } else if (d < 0.05) {
                return 0.05;
            } else {
                return d;
            }
        }
    }

}
