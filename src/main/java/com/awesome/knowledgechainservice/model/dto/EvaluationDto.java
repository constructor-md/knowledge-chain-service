package com.awesome.knowledgechainservice.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EvaluationDto {
    private String evaluation;
    private int score;
}
