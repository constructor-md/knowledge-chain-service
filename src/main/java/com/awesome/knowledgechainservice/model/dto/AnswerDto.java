package com.awesome.knowledgechainservice.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AnswerDto {
    private String id;
    private String answer;
    private String evaluation;
    private Integer score;
    private Long userId;
}
