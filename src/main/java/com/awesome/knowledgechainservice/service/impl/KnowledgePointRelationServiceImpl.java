package com.awesome.knowledgechainservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.awesome.knowledgechainservice.mapper.KnowledgePointRelationMapper;
import com.awesome.knowledgechainservice.model.dto.util.UndirectedConnection;
import com.awesome.knowledgechainservice.model.entity.KnowledgePointRelation;
import com.awesome.knowledgechainservice.service.KnowledgePointRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 82611
 * @description 针对表【knowledge_point_relation】的数据库操作Service实现
 * @createDate 2025-05-11 17:12:01
 */
@Service
public class KnowledgePointRelationServiceImpl extends ServiceImpl<KnowledgePointRelationMapper, KnowledgePointRelation>
        implements KnowledgePointRelationService {

    // 创建无向关系
    @Override
    @Transactional
    public void createRelation(UndirectedConnection<Long, Long> undirectedConnection) {
        // 不存在关系才添加
        if (!relationExist(undirectedConnection)) {
            KnowledgePointRelation knowledgePointRelation = new KnowledgePointRelation();
            knowledgePointRelation.setLeft(undirectedConnection.getU());
            knowledgePointRelation.setRight(undirectedConnection.getV());
            knowledgePointRelation.setCreateTime(new Date());
            knowledgePointRelation.setUpdateTime(new Date());
            knowledgePointRelation.setHighlight(0);
            baseMapper.insert(knowledgePointRelation);
        }
    }

    // 删除无向关系（双向删除）
    @Override
    @Transactional
    public void deleteRelation(UndirectedConnection<Long, Long> undirectedConnection) {
        // 存在关系才删除
        if (relationExist(undirectedConnection)) {
            baseMapper.delete(new QueryWrapper<KnowledgePointRelation>()
                    .lambda()
                    .eq(KnowledgePointRelation::getLeft, undirectedConnection.getU())
                    .eq(KnowledgePointRelation::getRight, undirectedConnection.getV()));
            baseMapper.delete(new QueryWrapper<KnowledgePointRelation>()
                    .lambda()
                    .eq(KnowledgePointRelation::getRight, undirectedConnection.getU())
                    .eq(KnowledgePointRelation::getLeft, undirectedConnection.getV()));
        }
    }

    // 返回指定知识点ID列表中所有关联关系（无向且去重）
    @Override
    @Transactional
    public List<String[]> findAllRelationsBetweenNodes(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<KnowledgePointRelation> knowledgePointRelationListA = baseMapper.selectList(new QueryWrapper<KnowledgePointRelation>()
                .lambda()
                .in(KnowledgePointRelation::getLeft, ids));
        List<KnowledgePointRelation> knowledgePointRelationListB = baseMapper.selectList(new QueryWrapper<KnowledgePointRelation>()
                .lambda()
                .in(KnowledgePointRelation::getRight, ids));
        Set<UndirectedConnection<Long, Long>> set = Stream.concat(knowledgePointRelationListA.stream(), knowledgePointRelationListB.stream())
                .map(k -> UndirectedConnection.create(k.getLeft(), k.getRight())).collect(Collectors.toSet());
        return set.stream().map(u -> new String[]{String.valueOf(u.getU()), String.valueOf(u.getV())}).collect(Collectors.toList());
    }

    // 查找与指定节点ID有关的节点ID列表
    @Override
    @Transactional
    public Set<Long> findRelatedKnowledgePointList(Long id) {
        List<String[]> connections = findAllRelationsBetweenNodes(Collections.singletonList(id));
        if (CollectionUtil.isEmpty(connections)) {
            return new HashSet<>();
        }
        return connections.stream()
                .flatMap(Arrays::stream)
                .filter(point -> !id.equals(Long.valueOf(point)))
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    // 删除指定节点的全部关系
    @Override
    @Transactional
    public void deleteRelations(List<Long> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            baseMapper.delete(new QueryWrapper<KnowledgePointRelation>()
                    .lambda()
                    .in(KnowledgePointRelation::getLeft, ids));
            baseMapper.delete(new QueryWrapper<KnowledgePointRelation>()
                    .lambda()
                    .in(KnowledgePointRelation::getRight, ids));
        }
    }

    // 判断无向关系是否存在
    @Override
    @Transactional
    public boolean relationExist(UndirectedConnection<Long, Long> undirectedConnection) {
        return baseMapper.existsRelation(undirectedConnection.getU(), undirectedConnection.getV());
    }
}




