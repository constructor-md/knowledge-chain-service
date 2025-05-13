package com.awesome.knowledgechainservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.awesome.knowledgechainservice.annotation.DataSource;
import com.awesome.knowledgechainservice.aop.UserInfoContext;
import com.awesome.knowledgechainservice.commons.Constants;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.config.datasource.DataSourceContextHolder;
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
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private KnowledgeInfoService knowledgeInfoService;

    @Override
    @Transactional
    @DataSource(value = DataSourceContextHolder.SALVE)
    public AnswerInfo queryByKIdAndUserId(Long kId, Long userId) {
        return this.lambdaQuery()
                .eq(AnswerInfo::getKId, kId)
                .eq(AnswerInfo::getUserId, userId).one();
    }

    @Override
    @Transactional
    @DataSource
    public void submit(AnswerDto answerDto) {
        AnswerInfo answerInfo = queryByKIdAndUserId(Long.valueOf(answerDto.getId()), answerDto.getUserId());
        if (answerInfo == null) {
            answerInfo = new AnswerInfo();
            answerInfo.setAnswer(answerDto.getAnswer());
            answerInfo.setKId(Long.valueOf(answerDto.getId()));
            answerInfo.setUserId(answerDto.getUserId());
            answerInfo.setCreateTime(new Date());
            answerInfo.setUpdateTime(new Date());
            baseMapper.insert(answerInfo);
        } else {
            // 提交新回答，更新回答，清除原本的评价
            answerInfo.setAnswer(answerDto.getAnswer());
            answerInfo.setEvaluation(null);
            answerInfo.setScore(null);
            answerInfo.setUpdateTime(new Date());
            baseMapper.updateById(answerInfo);
        }
    }

    @Override
    @Transactional
    @DataSource
    public void updateEvaluationAndScore(Long kId, Long userId, String evaluation, Integer score) {
        AnswerInfo answerInfo = queryByKIdAndUserId(kId, userId);
        answerInfo.setEvaluation(evaluation);
        answerInfo.setScore(score);
        baseMapper.updateById(answerInfo);
    }

    @Override
    @DataSource
    public void deleteByKId(Long kId) {
        baseMapper.delete(new QueryWrapper<AnswerInfo>().lambda().eq(AnswerInfo::getKId, kId));
    }

    @Override
    @DataSource
    public void deleteByKIdList(List<Long> kIdList) {
        baseMapper.delete(new QueryWrapper<AnswerInfo>().lambda().in(AnswerInfo::getKId, kIdList));
    }

    private static final String SYSTEM_PROMPT = "" +
            "我会给你一位热爱学习，注重学习成果的用户，关于一个知识点的习题的答案。" +
            "希望你能给予他公正的评价，要肯定他的学习进度，也要正确的指出他的错误，帮助他更好的学习这个知识点。" +
            "我给出的数据是JSON格式，包括如下内容：" +
            "{\n" +
            "    \"knowledge\": \"需要学习的知识文本\",\n" +
            "    \"question\":\"根据知识点给用户出的习题\",\n" +
            "    \"answer\":\"用户根据习题做出的回答\"\n" +
            "}" +
            "希望你给出的评价包括一段条理清晰的完全符合Markdown语法的评价结果文本，以及一个百分制下的评分（90分以上为优秀，80分以上为及格）。" +
            "你的评价文本需要注意换行，且符合markdown语法格式，因为它会被渲染成markdown。不要给出评价结果的标题，我的页面上有，会导致重复。" +
            "你会以流式返回数据，我希望返回的数据中的评价文本和评分之间用@分隔，评价文本中不会出现@，且评分是返回数据的最后的部分。" +
            "以下是示例返回结果：不错还行XXXXXAAAAABBBBB@90。" +
            "让我可以从中拆解出评价结果：不错还行XXXXXAAAAABBBBB。评分：90。";

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
                        // @ 之后的数据是评分
                        if (words.contains("@")) {
                            hasAt.set(true);
                            String[] data = words.split("@", -1);
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
                    int score = StrUtil.isBlank(scoreData.toString()) ? 0 : Integer.parseInt(scoreData.toString());
                    // 取出其中的评分 并保存结果
                    updateEvaluationAndScore(id, userInfo.getId(), fullData.toString(), score);
                });
    }

    @Override
    @DataSource(value = DataSourceContextHolder.SALVE)
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




