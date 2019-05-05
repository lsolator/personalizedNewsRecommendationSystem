package com.news.recommend.dao;


import com.news.recommend.entity.RecommendedWeight;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RWeightRepository extends Neo4jRepository<RecommendedWeight,Long> {
    Optional<RecommendedWeight> findByUid(@Param("uid") String uid);
}
