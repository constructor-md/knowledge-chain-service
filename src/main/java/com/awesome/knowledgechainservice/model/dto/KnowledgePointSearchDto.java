package com.awesome.knowledgechainservice.model.dto;

import cn.hutool.core.util.StrUtil;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import lombok.Data;

import java.util.Set;

@Data
public class KnowledgePointSearchDto {
    public String id;
    public String title;
    public String briefContent;
    public boolean relate;

    public static KnowledgePointSearchDto transferDto(KnowledgeInfo knowledgeInfo, Set<Long> relatedIdList) {
        KnowledgePointSearchDto knowledgePointSearchDto = new KnowledgePointSearchDto();
        knowledgePointSearchDto.setId(String.valueOf(knowledgeInfo.getId()));
        knowledgePointSearchDto.setTitle(knowledgeInfo.getTitle());
        String briefContent = knowledgeInfo.getMarkdown() == null ? "" : knowledgeInfo.getMarkdown();
        if (briefContent.length() > 12) {
            briefContent = StrUtil.sub(knowledgeInfo.getMarkdown(), 0, 30) + "...";
        }
        knowledgePointSearchDto.setBriefContent(briefContent);
        knowledgePointSearchDto.setRelate(relatedIdList.contains(knowledgeInfo.getId()));
        return knowledgePointSearchDto;
    }
}
