package com.awesome.knowledgechainservice.service;

import com.awesome.knowledgechainservice.model.dto.util.UndirectedConnection;
import com.awesome.knowledgechainservice.model.entity.KnowledgePointRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author 82611
 * @description 针对表【knowledge_point_relation】的数据库操作Service
 * @createDate 2025-05-11 17:12:01
 */
public interface KnowledgePointRelationService extends IService<KnowledgePointRelation> {

    void createRelation(UndirectedConnection<Long, Long> undirectedConnection);

    void deleteRelation(UndirectedConnection<Long, Long> undirectedConnection);

    List<String[]> findAllRelationsBetweenNodes(List<Long> ids);

    Set<Long> findRelatedKnowledgePointList(Long id);

    void deleteRelations(List<Long> ids);

    boolean relationExist(UndirectedConnection<Long, Long> undirectedConnection);
}
