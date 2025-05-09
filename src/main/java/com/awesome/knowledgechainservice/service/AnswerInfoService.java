package com.awesome.knowledgechainservice.service;

import com.awesome.knowledgechainservice.model.dto.AnswerDto;
import com.awesome.knowledgechainservice.model.dto.ai.SiliconFlowRequest;
import com.awesome.knowledgechainservice.model.entity.AnswerInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

/**
* @author 82611
* @description 针对表【answer_info】的数据库操作Service
* @createDate 2025-04-09 16:15:31
*/
public interface AnswerInfoService extends IService<AnswerInfo> {

    void submit(AnswerDto answerDto);

    void deleteByKId(Long kId);

    void deleteByKIdList(List<Long> kId);

    Flux<String> aiStreamProcess(SiliconFlowRequest request);

    Flux<ServerSentEvent<String>> getStreamEvaluationByV3(Long id);

    Integer getScore(Long id);

}
