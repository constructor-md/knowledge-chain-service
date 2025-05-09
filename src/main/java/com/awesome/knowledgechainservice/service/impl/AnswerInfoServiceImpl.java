package com.awesome.knowledgechainservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.awesome.knowledgechainservice.aop.UserInfoContext;
import com.awesome.knowledgechainservice.commons.Constants;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.exception.BusinessException;
import com.awesome.knowledgechainservice.exception.ErrorCode;
import com.awesome.knowledgechainservice.exception.ThrowUtils;
import com.awesome.knowledgechainservice.mapper.AnswerInfoMapper;
import com.awesome.knowledgechainservice.model.dto.AnswerDto;
import com.awesome.knowledgechainservice.model.dto.TestPaper;
import com.awesome.knowledgechainservice.model.dto.ai.ChatCompletionChunk;
import com.awesome.knowledgechainservice.model.dto.ai.SiliconFlowRequest;
import com.awesome.knowledgechainservice.model.entity.AnswerInfo;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.awesome.knowledgechainservice.model.entity.SysConfig;
import com.awesome.knowledgechainservice.model.entity.UserInfo;
import com.awesome.knowledgechainservice.service.AnswerInfoService;
import com.awesome.knowledgechainservice.service.KnowledgeInfoService;
import com.awesome.knowledgechainservice.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 82611
 * @description 针对表【answer_info】的数据库操作Service实现
 * @createDate 2025-04-09 16:15:31
 */
@Service
public class AnswerInfoServiceImpl extends ServiceImpl<AnswerInfoMapper, AnswerInfo>
        implements AnswerInfoService {

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private KnowledgeInfoService knowledgeInfoService;

    @Override
    @Transactional
    public void submit(AnswerDto answerDto) {
        AnswerInfo answerInfo = new AnswerInfo();
        answerInfo.setAnswer(answerDto.getAnswer());
        answerInfo.setEvaluation(answerDto.getEvaluation());
        answerInfo.setScore(answerDto.getScore());
        answerInfo.setKId(Long.valueOf(answerDto.getKId()));
        answerInfo.setUserId(answerDto.getUserId());
        answerInfo.setCreateTime(new Date());
        answerInfo.setUpdateTime(new Date());
        baseMapper.insertOrUpdate(answerInfo);
    }

    @Override
    public void deleteByKId(Long kId) {
        baseMapper.delete(new QueryWrapper<AnswerInfo>().lambda().eq(AnswerInfo::getKId, kId));
    }

    @Override
    public void deleteByKIdList(List<Long> kIdList) {
        baseMapper.delete(new QueryWrapper<AnswerInfo>().lambda().in(AnswerInfo::getKId, kIdList));
    }

    private static final String SYSTEM_PROMPT = "" +
            "我会给你一位热爱学习，注重学习成果的用户，关于一个知识点的习题的答案。" +
            "希望你能给予他公正的评价，要肯定他的学习进度，也要正确的指出他的错误，帮助他更好的学习这个知识点。" +
            "希望你给出的评价包括一段条理清晰的Markdown文本，以及一个百分制下的评分（90分以上为优秀，80分以上为及格）。" +
            "我给出的数据是JSON格式，包括如下内容：" +
            "{\n" +
            "    \"knowledge\": \"需要学习的知识文本\",\n" +
            "    \"question\":\"根据知识点给用户出的习题\",\n" +
            "    \"answer\":\"用户根据习题做出的回答\"\n" +
            "}" +
            "你会以流式返回数据，我希望返回的数据中的评价文本和评分之间用@分隔，评价文本中不会出现@，且评分是返回数据的最后的部分";

    private static final SiliconFlowRequest.Message systemMessage = new SiliconFlowRequest.Message()
            .setRole("system")
            .setContent(SYSTEM_PROMPT);

    private final WebClient webClient = WebClient.create("");

    private SiliconFlowRequest getAISuggestionStreamRequest() {
        List<SiliconFlowRequest.Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        return new SiliconFlowRequest()
                .setModel("deepseek-ai/DeepSeek-V3")
                .setStream(true)
                .setTemperature(0.8)
                .setTopP(1.0)
                .setN(1)
                .setMaxTokens(2048)
                .setMessages(messages)
                .setResponseFormat(new SiliconFlowRequest.ResponseFormat().setType("text"));
    }

    @Override
    public Flux<String> aiStreamProcess(SiliconFlowRequest request) {
        String chatUrl = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_CHAT_URL).one().getSysValue();
        String apiKey = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_API_KEY).one().getSysValue();

        return webClient.post()
                .uri(chatUrl)
                .headers(httpHeaders -> {
                    httpHeaders.set("Authorization", "Bearer " + apiKey);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(String.class);
    }


    @Override
    public Flux<ServerSentEvent<String>> getStreamEvaluationByV3(Long id) {
        UserInfo userInfo = UserInfoContext.get();
        // 查询知识点、问题和用户回答
        KnowledgeInfo knowledgeInfo = knowledgeInfoService.getById(id);
        AnswerInfo answerInfo = this.lambdaQuery()
                .eq(AnswerInfo::getKId, id)
                .eq(AnswerInfo::getUserId, UserInfoContext.get().getId())
                .one();
        ThrowUtils.throwIf(answerInfo == null, new BusinessException(ErrorCode.NOT_FOUND, "用户未提交回答"));

        TestPaper testPaper = new TestPaper();
        testPaper.setKnowledge(knowledgeInfo.getMarkdown());
        testPaper.setQuestion(knowledgeInfo.getQuestion());
        testPaper.setAnswer(answerInfo.getAnswer());

        SiliconFlowRequest request = getAISuggestionStreamRequest();
        SiliconFlowRequest.Message userMessage =
                new SiliconFlowRequest.Message()
                        .setRole("user")
                        .setContent(JSONUtil.toJsonStr(testPaper));
        request.getMessages().add(userMessage);

        Flux<String> responseFlux = aiStreamProcess(request);
        StringBuilder fullData = new StringBuilder();
        AtomicBoolean hasAt = new AtomicBoolean(false);
        StringBuilder scoreData = new StringBuilder();
        return responseFlux
                .flatMap(chunk -> {
                    String words = "";
                    if (!chunk.equals("[DONE]")) {
                        ChatCompletionChunk chatCompletionChunk = JSONUtil.parseObj(chunk).toBean(ChatCompletionChunk.class);
                        words = chatCompletionChunk.getChoices().get(0).getDelta().getContent();
                    }
                    // @ 之后的数据是评分
                    if (words.contains("@")) {
                        hasAt.set(true);
                        String[] data = words.split("@");
                        words = data[0];
                        fullData.append(data[0]);
                        scoreData.append(data[1]);
                    } else {
                        if (!hasAt.get()) {
                            fullData.append(words);
                        } else {
                            scoreData.append(words);
                            words = "";
                        }
                    }

                    return Flux.just(words)
                            .map(data -> ServerSentEvent.<String>builder()
                                    .data(JSONUtil.toJsonStr(R.ok(data)))
                                    .build());
                })
                .onErrorResume(error -> {
                    log.error("AI评价刷新发生异常", error);
                    return Flux.just(ServerSentEvent.<String>builder()
                            .data("请求发生异常，请稍后重试")
                            .build());
                })
                .doOnComplete(() -> {
                    // 取出其中的评分 并保存结果
                    AnswerDto answerDto = new AnswerDto();
                    answerDto.setEvaluation(fullData.toString());
                    answerDto.setKId(String.valueOf(id));
                    answerDto.setScore(Integer.parseInt(scoreData.toString()));
                    answerDto.setUserId(userInfo.getId());
                    submit(answerDto);
                });
    }

    @Override
    public Integer getScore(Long id) {
        // 查询用户对该知识点回答的AI评分
        AnswerInfo answerInfo = this.lambdaQuery()
                .eq(AnswerInfo::getKId, id)
                .eq(AnswerInfo::getUserId, UserInfoContext.get().getId())
                .one();
        ThrowUtils.throwIf(answerInfo == null, new BusinessException(ErrorCode.NOT_FOUND, "用户未提交回答"));
        return answerInfo.getScore();
    }
}




