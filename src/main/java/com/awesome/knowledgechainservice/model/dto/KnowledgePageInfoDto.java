package com.awesome.knowledgechainservice.model.dto;

import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import lombok.Data;

/**
 * 知识页信息
 */
@Data
public class KnowledgePageInfoDto {

    private String id;
    private String markdown;
    private String question;
    private String answer;
    private String evaluation;
    private int score;

    public static KnowledgePageInfoDto transferDto(KnowledgeInfo knowledgeInfo) {
        KnowledgePageInfoDto knowledgePageInfoDto = new KnowledgePageInfoDto();
        knowledgePageInfoDto.setId(String.valueOf(knowledgeInfo.getId()));
        knowledgePageInfoDto.setMarkdown(knowledgeInfo.getMarkdown());
        knowledgePageInfoDto.setQuestion(knowledgeInfo.getQuestion());
        return knowledgePageInfoDto;
    }

}
