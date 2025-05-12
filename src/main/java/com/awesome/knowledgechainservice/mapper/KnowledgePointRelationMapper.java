package com.awesome.knowledgechainservice.mapper;

import com.awesome.knowledgechainservice.model.entity.KnowledgePointRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author 82611
* @description 针对表【knowledge_point_relation】的数据库操作Mapper
* @createDate 2025-05-11 17:12:01
* @Entity com.awesome.knowledgechainservice.model.entity.KnowledgePointRelation
*/
public interface KnowledgePointRelationMapper extends BaseMapper<KnowledgePointRelation> {


    // 查询两点之间是否存在关系
    @Select("SELECT EXISTS ( " +
            "    SELECT 1 FROM knowledge_point_relation WHERE `left` = #{sourceId} AND `right` = #{targetId} " +
            "    UNION ALL " +
            "    SELECT 1 FROM knowledge_point_relation WHERE `left` = #{targetId} AND `right` = #{sourceId} " +
            ")")
    Boolean existsRelation(@Param("sourceId") Long sourceId, @Param("targetId") Long targetId);




}




