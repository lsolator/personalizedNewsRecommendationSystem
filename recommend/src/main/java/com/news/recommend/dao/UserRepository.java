package com.news.recommend.dao;

import com.news.recommend.entity.*;
import com.news.recommend.entity.result.CollectNews;
import com.news.recommend.entity.result.RecommendNews;
import com.news.recommend.entity.result.ShowUser;
import com.news.recommend.entity.result.TagAndNews;
import org.springframework.data.domain.Page;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User,Long> {
    Optional<User> findByUid(Long uid);

    //账号发现用户
    Optional<User> findByPhoneNumber(String number);

    //用户评论
    @Query(value = "MATCH p=(:User{uid:{0}})-[r:READ_IN]->(:News{nid:{1}}) " +
            "SET r.comment={2},r.commentTime={3} RETURN r.commentTime")
    String addNewsComment(Long uid, String nid, String comment, String commentTime);

    //用户收藏或者取消收藏
    @Query(value = "MATCH p=(:User{uid:{0}})-[r:READ_IN]->(:News{nid:{1}}) " +
            "SET r.isCollect={2},r.collectTime={3} RETURN r.collectTime")
    String alertNewsCollect(Long uid, String nid, Integer isCollect,String collectTime);

    //关注用户
    @Query(value = "MATCH (u1:User{uid:{0}}),(u2:User{uid:{1}}) CREATE (u1)-[r:ATTENTION_IN{attentionDate:{2}}]->(u2) RETURN r.attentionDate")
    String attentionUser(Long self, Long other,String attentionDate);

    //取消关注
    @Query(value = "MATCH (u1:User{uid:{0}})-[r]->(u2:User{uid:{1}}) delete r")
    void deleteAttention(Long self, Long other);

    //关注的用户列表
    @Query(value = "MATCH (u1:User{uid:{0}})-[r:ATTENTION_IN]->(u2:User) return u2.userName as userName,u2.phoneNumber as phoneNumber,u2.uid as uid order by r.attentionDate")
    List<ShowUser> showAttentionUser(Long self);

    //返回收藏的新闻
    @Query(value = "MATCH p=(:User{uid:{0}})-[r:READ_IN{isCollect:1}]->(n:News) RETURN n.title as title,r.collectTime as collectTime,n.nid as nid,n.date as date order by r.collectTime desc"
            ,countQuery = "MATCH p=(:User{uid:{0}})-[r:READ_IN{isCollect:1}]->(n:News) RETURN count(n)")
    Page<CollectNews> collectNews(Long uid, Pageable pageable);

    //用户浏览历史
    @Query(value = "MATCH p=(:User{uid:{0}})-[r:READ_IN]->(n:News) RETURN n.title as title,r.readDate as collectTime,n.nid as nid,n.date as date order by r.readDate desc"
            ,countQuery = "MATCH p=(:User{uid:{0}})-[r:READ_IN]->(n:News) RETURN count(n)")
    Page<CollectNews> newsLog(Long uid, Pageable pageable);

    //关注话题
    @Query(value = "MATCH (u:User{uid:{0}}),(t:Tag{tagName:{1}}) CREATE (u)-[r:LIKE]->(t)")
    void attentionTopic(Long uid, String tagName);

    //取消话题关注
    @Query(value = "MATCH (u1:User{uid:{0}})-[r]->(t:Tag{tagName:{1}}) delete r")
    void deleteAttentionTopic(Long uid, String tagName);

    //关注的话题数量
    @Query(value = "MATCH (u1:User{uid:{0}})-[r]->(t:Tag) return count(t)")
    Long numOfAttentionTopic(Long uid);

    @Query(value = "MATCH (u1:User{uid:{0}})-[r]->(t:Tag{tagName:{1}}) return count(t)")
    Long isAttentionTag(Long uid,String tagName);

    //关注的话题列表
    @Query(value = "match(:User{uid:{0}})-[:LIKE]->(t:Tag) return t")
    List<Tag> showAttentionTag(Long uid);

    //关注的话题所推荐的新闻
    @Query(value = "MATCH (u:User{uid:{0}})-[:LIKE]->(t:Tag)-[:OWNED_IN] ->(n:News) " +
            "where not (u)-[:READ_IN]->(n) " +
            "return n.title as title,n.readNum as readNum,n.date as date,t.tagName as tagName,n.nid as nid,n.newsTime as newsTime order by readNum desc,date desc skip {1} limit {2}")
    List<TagAndNews> topicRecommend(Long uid, Integer startNum, Integer num);

    //协同推荐  不是互相关注的用户 拥有相同的收藏记录 然后推荐新闻
    @Query(value = "match (u:User{uid:{0}}) match (u)-[:READ_IN{isCollect:1}]->(n:News) match (n)<-[:READ_IN{isCollect:1}]-(u1:User) match (u1)-[r:READ_IN]->(nn:News) " +
            "where not (u)-[:READ_IN]->(nn) with distinct nn,count(r) as num " +
            "return nn.title as title,nn.readNum as readNum,nn.date as date,nn.nid as nid,nn.newsTime as newsTime " +
            "order by num desc limit {1}")
    List<RecommendNews> recommendBySimilarNewsLog(Long uid, Integer limit);

    //协同推荐  推荐没有关注过的Tag的浏览量最大的文章
    @Query(value = "match (u:User{uid:{0}}) match (u)-[:LIKE]->(t:Tag) " +
            "match (t)<-[:LIKE]-(u1:User) match (u1)-[r:LIKE]->(tt:Tag) " +
            "where not (u)-[:LIKE]->(tt) with distinct tt,u match (tt)-->(n:News) where not (u)-[:READ_IN]->(n) " +
            "return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime " +
            "order by readNum desc limit {1}")
    List<RecommendNews> recommendBySimilarTag(Long uid, Integer limit);

    //用户关注对象最近浏览的文章
    @Query(value = "match (u:User{uid:{0}})-[:ATTENTION_IN]->(u1:User)-[r:READ_IN]->(n:News) " +
            "where not (u)-[:READ_IN]-(n) " +
            "return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime " +
            "order by r.readDate desc limit {1}")
    List<RecommendNews> recommendByAttentionUserLook(Long uid, Integer limit);

    //图算法推荐  寻找用户在[3,5]内找到的新闻
    @Query(value = "match (u:User{uid:{0}})-[*3..5]->(n:News) where not (u)-->(n)  " +
            "return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime " +
            "order by date desc,readNum desc limit {1}")
    List<RecommendNews> recommendMapFunction(Long uid, Integer limit);

    //相似新闻推荐 通过关键词
    @Query(value = "match (t:Tag)-[o:OWNED_IN]->(n:News) where t.tagName in {1} with count(o) as num,n " +
            "where not (:User{uid:{0}})-[:READ_IN]-(n) " +
            "return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime " +
            "order by num DESC,date DESC,readNum DESC limit {2}")
    List<RecommendNews> recommendByPrefWord(Long uid,String[] prefWord,Integer limit);

    //他关注的用户 数量
    @Query(value = "MATCH (u1:User{uid:{0}})-[r:ATTENTION_IN]->(u2:User) return count(u2)")
    Long attentionUser(Long uid);

    //粉丝用户关注他 数量
    @Query(value = "MATCH (u1:User{uid:{0}})<-[r:ATTENTION_IN]-(u2:User) return count(u2)")
    Long userAttention(Long uid);

    //是否关注此用户
    @Query(value = "MATCH (u1:User{uid:{0}})-[r]->(u2:User{uid:{1}}) return r.attentionDate")
    String isAttentionUser(Long uid, Long otherId);
}

