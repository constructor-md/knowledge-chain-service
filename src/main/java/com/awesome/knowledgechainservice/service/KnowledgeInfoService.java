package com.awesome.knowledgechainservice.service;

import com.awesome.knowledgechainservice.model.dto.KnowledgePointInfoDto;
import com.awesome.knowledgechainservice.model.dto.KnowledgePointSearchDto;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 82611
 * @description 针对表【knowledge_info】的数据库操作Service
 * @createDate 2025-04-09 16:15:38
 */
public interface KnowledgeInfoService extends IService<KnowledgeInfo> {

    KnowledgeInfo addKnowledgePoint(KnowledgePointInfoDto knowledgePointInfoDto);

    void updateLocationList(List<KnowledgePointInfoDto> knowledgePointInfoDtoList);

    void delete(Long id);

    void deleteByKbId(Long id);

    String generateQuestionByAI(Long id);

    List<KnowledgePointSearchDto> search(Long nowId, String keywords);

}
