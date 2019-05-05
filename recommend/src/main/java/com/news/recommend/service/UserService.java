package com.news.recommend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.news.recommend.dao.UserRepository;
import com.news.recommend.entity.PrefWord;
import com.news.recommend.entity.Tag;
import com.news.recommend.entity.result.CollectNews;
import com.news.recommend.entity.User;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.entity.result.ShowUser;
import com.news.recommend.entity.result.TagAndNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注册or更新用户服务
     *
     * @param user
     */
    public void SaveUser(User user) {
        userRepository.save(user);
    }

    public User findUser(String uid) {
        Optional<User> optionalUser = userRepository.findByUid(Long.valueOf(uid));
        return optionalUser.orElse(null);
    }

    public User findByPhoneNum(String phone) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phone);
        return optionalUser.orElse(null);
    }

    /**
     * 用户添加评论
     *
     * @param uid
     * @param nid
     * @param comment
     * @return
     */
    public Boolean AddNewsComment(String uid, String nid, String comment) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = userRepository.addNewsComment(Long.valueOf(uid), nid, comment, sdf.format(new Date()));
        if (date != null) {
            System.out.println(date);
            return true;
        } else return false;
    }

    /**
     * 用户收藏新闻跟取消收藏
     *
     * @param uid
     * @param nid
     * @param isCollect
     * @return
     */
    public Boolean AlertIsCollect(String uid, String nid, Integer isCollect) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = userRepository.alertNewsCollect(Long.valueOf(uid), nid, isCollect, sdf.format(new Date()));
        if (date != null) {
            System.out.println(date);
            return true;
        } else return false;
    }

    /**
     * 返回用户收藏的新闻  分页
     * @param uid
     * @param pageable
     * @return
     */
    public Map<String,Object> UserCollectNews(String uid,Pageable pageable) {
        HashMap<String, Object> map = new HashMap<>(3);
        Page<CollectNews> newsPage = userRepository.collectNews(Long.valueOf(uid), pageable);
        map.put("total", newsPage.getTotalPages());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    /**
     * 用户浏览历史  分页
     * @param uid
     * @param pageable
     * @return
     */
    public Map<String,Object> UserReadLogs(String uid,Pageable pageable) {
        Page<CollectNews> newsPage = userRepository.newsLog(Long.valueOf(uid), pageable);
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("total", newsPage.getTotalPages());
        map.put("page", pageable.getPageNumber());
        map.put("list", newsPage.getContent());
        return map;
    }

    /**
     * 关注用户
     * @param selfUid
     * @param otherUid
     */
    public void AttentionUser(String selfUid, String otherUid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        userRepository.attentionUser(Long.valueOf(selfUid), Long.valueOf(otherUid), sdf.format(new Date()));
    }

    /**
     * 取消关注
     * @param selfUid
     * @param otherUid
     */
    public void DeleteAttentionUser(String selfUid, String otherUid) {
        userRepository.deleteAttention(Long.valueOf(selfUid), Long.valueOf(otherUid));
    }

    /**
     * 展示用户关注列表
     * @param selfUid
     * @return
     */
    public List<ShowUser> ShowAttentionUserList(String selfUid) {
        return userRepository.showAttentionUser(Long.valueOf(selfUid));
    }

    /**
     * 关注话题
     * @param uid
     * @param tagName
     */
    public void AttentionTopic(String uid,String tagName) {
        userRepository.attentionTopic(Long.valueOf(uid), tagName);
    }

    /**
     * 取消关注
     * @param uid
     * @param tagName
     */
    public void DeleteAttentionTopic(String uid, String tagName) {
        userRepository.deleteAttentionTopic(Long.valueOf(uid), tagName);
    }

    /**
     * 关注的主题推荐
     * @param uid
     * @param startNum
     * @param num
     * @return
     */
    public List<TagAndNews> TopicRecommend(String uid, Integer startNum, Integer num) {
        return userRepository.topicRecommend(Long.valueOf(uid), startNum, num);
    }

    //用户关注其它用户的数量
    public Integer UserAttention(String uid) {
        return userRepository.userAttention(Long.valueOf(uid)).intValue();
    }

    //其他用户关注用户的数量
    public Integer AttentionUser(String uid) {
        return userRepository.attentionUser(Long.valueOf(uid)).intValue();
    }

    //关注的话题列表
    public List<Tag> ShowAttentionTags(String uid) {
        return userRepository.showAttentionTag(Long.valueOf(uid));
    }

    //用户1是否关注用户2
    public String IsAttentionUser(String uid, String otherId) {
        return userRepository.isAttentionUser(Long.valueOf(uid), Long.valueOf(otherId));
    }

    //用户是否关注主题
    public Integer IsAttentionTag(String uid, String tagName) {
        return userRepository.isAttentionTag(Long.valueOf(uid), tagName).intValue();
    }

}
