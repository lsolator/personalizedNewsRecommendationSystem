package com.news.recommend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hankcs.hanlp.HanLP;
import com.news.recommend.dao.NewsRepository;
import com.news.recommend.dao.TagRepository;
import com.news.recommend.dao.UserRepository;
import com.news.recommend.entity.*;
import com.news.recommend.entity.result.Comment;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.entity.result.TagAndNews;
import com.news.util.IdGen;
import com.news.util.PrefWordsGen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NewService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private TagRepository tagRepository;

    private final NewsRepository newsRepository;

    public NewService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    //新增新闻信息
    public void SaveNews(String title, String content, String type) {
        String nid = String.valueOf(IdGen.get().nextId());
        String contentR = content.replaceAll("\n", "<br/>");
        News news = new News();
        news.setNid(nid);
        news.setTitle(title);
        news.setContent(contentR);
        news.setDate(new Date());
        news.setNewsTime(getNewsTime());
        news.setReadNum(0);
        newsRepository.save(news);
        //该新闻主题
        Tag tag = tagRepository.findTagByTagName(type);
        TagToNew tt = new TagToNew(1, tag, news);
        news.addTagToNew(tt);
        //提取关键词
        List<String> keywordList = HanLP.extractKeyword(content, 3);
        for (String s : keywordList) {
            Tag tagKeyword = tagRepository.findTagByTagName(s);
            if (tagKeyword != null) {
                TagToNew tagToNew = new TagToNew(2, tagKeyword, news);
                news.addTagToNew(tagToNew);
            } else {
                Tag t = new Tag(s, 2);
                tagRepository.save(t);
                TagToNew tagToNew = new TagToNew(2, t, news);
                news.addTagToNew(tagToNew);
            }
        }
        newsRepository.save(news);
    }

    //修改新闻信息
    public void AlertNews(String nid, String title, String content) {
        Optional<News> optional = newsRepository.findByNid(nid);
        News news = optional.get();
        news.setTitle(title);
        news.setContent(content);
        newsRepository.save(news);
    }

    //删除新闻
    public void DeleteNews(String nid) {
        newsRepository.deleteNews(nid);
    }

    /**
     * 读取文章  1、不是用户就会单纯的增加文章阅读量 2、是用户的话会更新用户偏好词 相同文章不会再更新
     *
     * @param uid
     * @param nid
     * @return
     */
    public News ReadNews(String uid, String nid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Optional<News> optionalNews = newsRepository.findByNid(nid);
        if (optionalNews.isPresent()) {
            News news = optionalNews.get();
            if (uid != null && newsRepository.exitsNewsLog(Long.valueOf(uid), nid) == null) {
                Optional<User> optionalUser = userRepository.findByUid(Long.valueOf(uid));
                User user = optionalUser.get();
                //更新偏好词
                String prefWord = handlePrefWord(user.getPrefWords(), news.getTagToNews());
                user.setPrefWords(prefWord);
                userRepository.save(user);
                NewsLog newsLog = new NewsLog(sdf.format(new Date()), news, user);
                //生成浏览记录
                news.addNewsLog(newsLog);
            }
            news.setReadNum(news.getReadNum() + 1);
            newsRepository.save(news);
            return news;
        } else return null;
    }

    /**
     * 热点新闻  时间段 分页
     *
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    public Map<String, Object> HotNews(String startDate, String endDate, Pageable pageable) {
        HashMap<String, Object> map = new HashMap<>(3);
        Page<ShowNews> newsPage = newsRepository.findHotNewsBetween(startDate, endDate, pageable);
        map.put("total", newsPage.getTotalPages());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    /**
     * 相似新闻 数量
     *
     * @param nid
     * @param num
     * @return
     */
    public List<ShowNews> MostFamiliarNewsList(String nid, Integer num) {
        return newsRepository.findMostFamiliarNewsList(nid, num);
    }

    /**
     * 主题新闻推荐  时间段 数量
     *
     * @param tagName
     * @param startDate
     * @param endDate
     * @param num
     * @return
     */
    public Map<String, Object> TopicNewsRecom(String tagName, String startDate, String endDate, Integer num) {
        Collection<News> news = newsRepository.findSameTopicNews(tagName, startDate, endDate, num);
        if (!news.isEmpty()) {
            return toFormat(news);
        } else {
            return null;
        }
    }

    /**
     * 搜索使用 用户关键词得到列表
     *
     * @param keyword
     * @param pageable
     * @return
     */
    public Map<String, Object> FindNewsByKeyWord(String keyword, Pageable pageable) {
        HashMap<String, Object> map = new HashMap<>(3);
        String s = ".*" + keyword + ".*";
        Page<ShowNews> newsPage = newsRepository.findNewsByKeyWord(s, pageable);
        map.put("total", newsPage.getTotalPages());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    /**
     * 更新用户偏好词  最多有30个偏好词 偏好的阈值为10
     *
     * @param prefWords
     * @param tagToNews
     * @return
     */
    private String handlePrefWord(String prefWords, List<TagToNew> tagToNews) {
        Gson gson = new Gson();
        Type setType = new TypeToken<HashSet<PrefWord>>() {
        }.getType();
        HashSet<PrefWord> hashSet = gson.fromJson(prefWords, setType);
        HashMap<String, PrefWord> hashMap = new HashMap<>();
        //得到user的偏好词列表  并且遍历放进map保证偏好唯一
        if (hashSet != null) {
            for (PrefWord p : hashSet
            ) {
                hashMap.put(p.getWord(), p);
            }
        }
        for (TagToNew t : tagToNews
        ) {
            String tagName = t.getTag().getTagName();
            if (t.getType() != 1) {
                if (hashMap.containsKey(tagName)) {
                    PrefWord p = hashMap.get(tagName);
                    p.setWordNum(p.getWordNum() * 0.9);
                } else {
                    PrefWord p = new PrefWord(tagName, 0.5);
                    hashMap.put(tagName, p);
                }
            }
        }
        PrefWordsGen prefWordsGen = new PrefWordsGen();
        prefWordsGen.addPrefWord(hashMap.values());
        return prefWordsGen.getPrefWord();
    }

    /**
     * 返回经过处理news  主要包括 title,datetime,readNum,nid
     *
     * @param news
     * @return
     */
    private Map<String, Object> toFormat(Collection<News> news) {
        List<Map<String, String>> list = new ArrayList<>();
        Iterator<News> result = news.iterator();
        while (result.hasNext()) {
            News news1 = result.next();
            list.add(map("title", news1.getTitle(), "nid", news1.getNid()
                    , "datetime", news1.getDateTime()
                    , "readNum", String.valueOf(news1.getReadNum())));
        }
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("news", list);
        return map;
    }

    private Map<String, String> map(String key1, String value1, String key2, String value2
            , String key3, String value3, String key4, String value4) {
        Map<String, String> result = new HashMap<String, String>(4);
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        result.put(key4, value4);
        return result;
    }


    /**
     * 返回最新的新闻
     *
     * @param num
     * @return
     */
    public List<ShowNews> LatestNews(Integer num) {
        return newsRepository.latestNews(num);
    }

    /**
     * 返回新闻的评论
     *
     * @param nid
     * @return
     */
    public List<Comment> CommentList(String nid) {
        return newsRepository.newsComment(nid);
    }

    //是否收藏了这篇文章
    public Boolean isCollect(String uid, String nid) {
        String s = newsRepository.isCollect(Long.valueOf(uid), nid);
        if (s != null) return true;
        return false;
    }

    //管理员查看新闻
    public Map<String, Object> returnNews(Pageable pageable) {
        HashMap<String, Object> map = new HashMap<>(3);
        Page<TagAndNews> newsPage = newsRepository.returnNews(pageable);
        map.put("count", newsPage.getTotalElements());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    public News getOnNews(String nid) {
        Optional<News> optionalNews = newsRepository.findByNid(nid);
        return optionalNews.get();
    }

    private String getNewsTime() {
        Calendar cal = Calendar.getInstance();
//        int hour = cal.get(Calendar.HOUR);
//        int minute = cal.get(Calendar.MINUTE);
//        int second = cal.get(Calendar.SECOND);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String currentTime = sdf.format(cal.getTime());
        return currentTime;
    }
}
