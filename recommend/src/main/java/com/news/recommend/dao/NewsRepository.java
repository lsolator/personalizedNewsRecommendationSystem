package com.news.recommend.dao;

import com.news.recommend.entity.News;
import com.news.recommend.entity.result.Comment;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.entity.result.TagAndNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//@Repository
public interface NewsRepository extends Neo4jRepository<News, Long> {
    Optional<News> findByNid(@Param("nid") String nid);

    @Query(value = "MATCH (n:News{nid:{0}})-[r]-() DELETE n,r")
    Long deleteNews(String nid);

    /*是否过阅读该新闻*/
    @Query(value = "MATCH p=(:User{uid:{0}})-[r:READ_IN]->(:News{nid:{1}}) RETURN r.readDate")
    String exitsNewsLog(Long uid, String nid);

    //返回时间段的热点新闻 分页
    @Query(value = "MATCH (n:News) where {0}<n.date<={1} and n.readNum>66 RETURN n.title as title,n.date as date,n.readNum as readNum,n.newsTime as newsTime,n.nid as nid " +
            "order by n.readNum DESC, n.date DESC ",countQuery = "MATCH (n:News) where {0}<n.date<={1} and n.readNum>66 RETURN count(n)")
    Page<ShowNews> findHotNewsBetween(String startDate, String endDate, Pageable pageable);

    //用户输入查找关键字
    @Query(value = "match(t:Tag)-[*0..1]->(nn:News) where t.tagName=~{0} or nn.title=~{0} or nn.content=~{0} with distinct nn " +
            "return nn.title as title,nn.readNum as readNum,nn.date as date,nn.nid as nid,nn.newsTime as newsTime,SUBSTRING(nn.content,0,100) as content order by readNum DESC,date desc "
            ,countQuery = "match(t:Tag)-[*0..1]->(nn:News) where t.tagName=~{0} or nn.title=~{0} or nn.content=~{0} with distinct nn return count(nn)")
    Page<ShowNews> findNewsByKeyWord(String keyword,Pageable pageable);

    //与nid 存在共同之处的最相似新闻
    @Query(value = "MATCH (:News{nid:{0}})<-[:OWNED_IN]-(t:Tag)-[:OWNED_IN]->(n:News) with n,count(t) as num return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime,num order by num DESC,readNum DESC limit {1}")
    List<ShowNews> findMostFamiliarNewsList(String nid, Integer num);

    /**
     * 主题新闻推荐（阅读量）  时间段 条数
     * @param tagName
     * @param startDate
     * @param endDate
     * @param num
     * @return
     */
    @Query(value = "match (t:Tag{tagName:{0}}) -[o:OWNED_IN{type:1}]->(n:News) where n.date>={1} and n.date<={2} return n order by n.readNum DESC, n.date DESC  limit {3}")
    Collection<News> findSameTopicNews(String tagName, String startDate, String endDate,Integer num);

    /**
     * 更新新闻
     * @param nid
     * @param date
     * @return
     */
    @Query(value = "match (n:News) where n.nid={0} set n.date={1} return n.id")
    Long updateNewsDate(String nid, String date);

    //最新新闻
    @Query(value = "match (n:News) return n.title as title,n.date as date,n.readNum as readNum,n.newsTime as newsTime,n.nid as nid  order by n.date desc,n.newsTime desc limit {0}")
    List<ShowNews> latestNews(Integer num);

    //新闻评论
    @Query(value = "match(:News{nid:{0}})<-[r:READ_IN]-(u:User) where r.commentTime is not null return u.uid as uid,u.userName as userName,r.comment as comment,r.commentTime as  commentTime")
    List<Comment> newsComment(String nid);

    //是否收藏了该新闻
    @Query(value = "MATCH (:User{uid:{0}})-[r:READ_IN]->(:News{nid:{1}}) where r.isCollect=1  RETURN r.collectTime")
    String isCollect(Long uid, String nid);

    //返回时间段的热点新闻 分页
    @Query(value = "MATCH (n:News) where {0}<n.date<={1} and n.readNum>66 and not (:User{uid:{3}})-->(n) RETURN n.title as title,n.date as date,n.readNum as readNum,n.newsTime as newsTime,n.nid as nid " +
            "order by n.readNum DESC, n.date DESC ",countQuery = "MATCH (n:News) where {0}<n.date<={1} and n.readNum>66 and not (:User{uid:{3}})-->(n) RETURN count(n)")
    Page<ShowNews> HotNewsRecommend(String startDate, String endDate, Pageable pageable,Long uid);

    //返回所有新闻
    @Query(value = "MATCH (n:News)<-[:OWNED_IN]-(t:Tag{type:1})  RETURN n.title as title,n.date as date,n.readNum as readNum,n.newsTime as newsTime,n.nid as nid,t.tagName as tagName " +
            "order by n.date DESC ",countQuery = "MATCH (n:News) RETURN count(n)")
    Page<TagAndNews> returnNews(Pageable pageable);
}
