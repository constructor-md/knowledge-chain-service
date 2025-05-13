package com.awesome.knowledgechainservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.awesome.knowledgechainservice.annotation.DataSource;
import com.awesome.knowledgechainservice.mapper.KnowledgeBaseInfoMapper;
import com.awesome.knowledgechainservice.model.dto.KnowledgePointInfoDto;
import com.awesome.knowledgechainservice.model.entity.KnowledgeBaseInfo;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.awesome.knowledgechainservice.service.AnswerInfoService;
import com.awesome.knowledgechainservice.service.KnowledgeBaseInfoService;
import com.awesome.knowledgechainservice.service.KnowledgeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 82611
 * @description 针对表【knowledge_base_info】的数据库操作Service实现
 * @createDate 2025-04-09 16:15:35
 */
@Service
public class KnowledgeBaseInfoServiceImpl extends ServiceImpl<KnowledgeBaseInfoMapper, KnowledgeBaseInfo>
        implements KnowledgeBaseInfoService {

    @Resource
    private KnowledgeInfoService knowledgeInfoService;

    @Resource
    private AnswerInfoService answerInfoService;

    @Override
    @Transactional
    @DataSource
    public KnowledgeBaseInfo addKnowledgeBaseInfo(String name) {

        // 新增知识库
        KnowledgeBaseInfo knowledgeBaseInfo = new KnowledgeBaseInfo();
        knowledgeBaseInfo.setName(name);
        knowledgeBaseInfo.setCreateTime(new Date());
        knowledgeBaseInfo.setUpdateTime(new Date());
        baseMapper.insert(knowledgeBaseInfo);

        // 为该知识库构建第一个初始知识点
        KnowledgePointInfoDto knowledgePointInfoDto = new KnowledgePointInfoDto();
        knowledgePointInfoDto.setKbId(String.valueOf(knowledgeBaseInfo.getId()));
        knowledgePointInfoDto.setTitle("初始知识点");
        knowledgePointInfoDto.setX(0);
        knowledgePointInfoDto.setY(0);
        knowledgePointInfoDto.setZ(0);
        knowledgeInfoService.addKnowledgePoint(knowledgePointInfoDto);

        return knowledgeBaseInfo;
    }

    @Override
    @Transactional
    @DataSource
    public boolean deleteKnowledgeBaseInfo(Long id) {

        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService
                .lambdaQuery()
                .eq(KnowledgeInfo::getKbId, id)
                .list();
        List<Long> kIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getId).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(kIdList)) {
            // 删除所有用户回答
            answerInfoService.deleteByKIdList(kIdList);
        }

        // 删除所有知识点
        knowledgeInfoService.deleteByKbId(id);

        // 删除知识库
        baseMapper.deleteById(id);
        return true;
    }
}




