package com.news.recommend.dao;

import com.news.recommend.entity.Tag;
import com.news.recommend.entity.result.ShowNews;
import com.news.recommend.entity.result.TagAndNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface TagRepository extends Neo4jRepository<Tag,Long> {
    Tag findTagByTagName(String tagName);

    //返回新闻的tag 包括分类和关键字两种选项  标签栏目需要
    @Query(value = "match (t:Tag{type:{1}})-->(n:News{nid:{0}}) return t")
    List<Tag> newsHotWord(String nid,Integer type);

    @Query(value = "match (t:Tag{type:1}) return t")
    List<Tag> findAllTag();

    //话题页的出现频率最高的10个词
    @Query(value = "MATCH (t:Tag)-[o:OWNED_IN{type:2}]->(nn:News) where nn.date>={0} and nn.date<={1} return t ,count(0) as num order by num desc limit 10")
    List<Tag> hotTopicList(String startDate, String endDate);

    //发现页的主题新闻  话题的主题新闻
    @Query(value = "match (t:Tag{tagName:{0}})-[:OWNED_IN{type:{1}}]->(n:News) " +
            "return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime " +
            "order by date desc,readNum desc",countQuery = "match (t:Tag{tagName:{0}})-[:OWNED_IN{type:{1}}]->(n:News) return count(n)")
    Page<ShowNews> topicNewsList(String tagName, Integer type,Pageable pageable);

    //时间段最热的新闻话题的前10 所得到的阅读量由多到少的新闻 话题新闻的主页
    @Query(value = "MATCH p= (t:Tag)-[o:OWNED_IN{type:2}]->(:News) with t,count(o) as num order by num desc limit 10 match (t)-->(n:News) where {0}<=n.date<={1} " +
            "with distinct n return n.title as title,n.readNum as readNum,n.date as date,n.nid as nid,n.newsTime as newsTime " +
            "order by readNum desc",countQuery = "MATCH p= (t:Tag)-[o:OWNED_IN{type:2}]->(:News) with t,count(o) as num order by num desc limit 10 match (t)-->(n:News) where {0}<=n.date<={1} with distinct n return count(n)")
    Page<ShowNews> hotTopicNewsList(String startDate, String endDate, Pageable pageable);
}
