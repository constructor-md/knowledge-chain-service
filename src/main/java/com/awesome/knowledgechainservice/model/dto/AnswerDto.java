package com.awesome.knowledgechainservice.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AnswerDto {
    private String answer;
    private String kId;
    private String evaluation;
    private Integer score;
    private Long userId;
}
