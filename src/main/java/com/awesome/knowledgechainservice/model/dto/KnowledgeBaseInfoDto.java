package com.awesome.knowledgechainservice.model.dto;

import com.awesome.knowledgechainservice.model.entity.KnowledgeBaseInfo;
import lombok.Data;

@Data
public class KnowledgeBaseInfoDto {

    private String id;
    private String name;

    public static KnowledgeBaseInfoDto transferDto(KnowledgeBaseInfo knowledgeBaseInfo) {
        KnowledgeBaseInfoDto knowledgeBaseInfoDto = new KnowledgeBaseInfoDto();
        knowledgeBaseInfoDto.setId(String.valueOf(knowledgeBaseInfo.getId()));
        knowledgeBaseInfoDto.setName(knowledgeBaseInfo.getName());
        return knowledgeBaseInfoDto;
    }

}
