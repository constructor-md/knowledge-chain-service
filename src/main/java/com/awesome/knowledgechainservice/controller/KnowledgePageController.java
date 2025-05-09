package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Auth;
import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.aop.UserInfoContext;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.dto.AnswerDto;
import com.awesome.knowledgechainservice.model.dto.KnowledgePageInfoDto;
import com.awesome.knowledgechainservice.model.entity.AnswerInfo;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.awesome.knowledgechainservice.service.AnswerInfoService;
import com.awesome.knowledgechainservice.service.KnowledgeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 知识页API
 */
@RestController
@RequestMapping("/page")
@Slf4j
public class KnowledgePageController {

    @Resource
    private KnowledgeInfoService knowledgeInfoService;

    @Resource
    private AnswerInfoService answerInfoService;

    /**
     * 获取知识页
     */
    @Login
    @GetMapping("/info")
    public R<KnowledgePageInfoDto> getKnowledgePage(@RequestParam("id") String id) {
        Long kId = Long.valueOf(id);
        KnowledgeInfo knowledgeInfo = knowledgeInfoService.getById(kId);
        KnowledgePageInfoDto knowledgePageInfoDto = KnowledgePageInfoDto.transferDto(knowledgeInfo);
        AnswerInfo answerInfo = answerInfoService.lambdaQuery()
                .eq(AnswerInfo::getKId, id)
                .eq(AnswerInfo::getUserId, UserInfoContext.get().getId())
                .one();
        knowledgePageInfoDto.setAnswer(answerInfo.getAnswer());
        knowledgePageInfoDto.setEvaluation(answerInfo.getEvaluation());
        knowledgePageInfoDto.setScore(answerInfo.getScore());
        return R.ok(knowledgePageInfoDto);
    }

    /**
     * 编辑 Markdown
     */
    @Login
    @Auth
    @PutMapping("/markdown")
    public R<?> updateMarkdown(@RequestBody KnowledgePageInfoDto knowledgePageInfoDto) {
        knowledgeInfoService.lambdaUpdate()
                .eq(KnowledgeInfo::getId, Long.valueOf(knowledgePageInfoDto.getId()))
                .set(KnowledgeInfo::getMarkdown, knowledgePageInfoDto.getMarkdown())
                .set(KnowledgeInfo::getUpdateTime, new Date()).update();
        return R.ok();
    }

    /**
     * 为知识点调用 AI 生成问题
     */
    @Login
    @Auth
    @PutMapping("/question")
    public R<String> generateQuestionByAI(@RequestParam("id") String id) {
        String question = knowledgeInfoService.generateQuestionByAI(Long.valueOf(id));
        return R.ok(question);
    }

}
