package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.aop.UserInfoContext;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.dto.AnswerDto;
import com.awesome.knowledgechainservice.service.AnswerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 知识点问答API
 */
@RestController
@RequestMapping("/qa")
@Slf4j
public class AnswerController {

    @Resource
    private AnswerInfoService answerInfoService;


    /**
     * 用户提交答案
     */
    @PostMapping("/answer")
    @Login
    public R<?> submitAnswer(@RequestBody AnswerDto answerDto) {
        answerDto.setUserId(UserInfoContext.get().getId());
        answerInfoService.submit(answerDto);
        return R.ok();
    }

    /**
     * 流式生成AI评价和评分并保存
     */
    @GetMapping(path = "/evaluation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Login
    public Flux<ServerSentEvent<String>> getAIEvaluation(@RequestParam("id") String id) {
        return answerInfoService.getStreamEvaluationByV3(Long.valueOf(id));
    }

    /**
     * 获取AI评分
     */
    @GetMapping("/score")
    @Login
    public R<Integer> getAIScore(@RequestParam("id") String id) {
        return R.ok(answerInfoService.getScore(Long.valueOf(id)));
    }

}
