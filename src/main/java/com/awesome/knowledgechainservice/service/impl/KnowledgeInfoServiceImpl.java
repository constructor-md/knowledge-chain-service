package com.awesome.knowledgechainservice.service.impl;

import com.awesome.knowledgechainservice.commons.Constants;
import com.awesome.knowledgechainservice.exception.BusinessException;
import com.awesome.knowledgechainservice.exception.ErrorCode;
import com.awesome.knowledgechainservice.mapper.KnowledgeInfoMapper;
import com.awesome.knowledgechainservice.model.dto.KnowledgePointInfoDto;
import com.awesome.knowledgechainservice.model.dto.ai.SiliconFlowRequest;
import com.awesome.knowledgechainservice.model.dto.ai.SiliconFlowResponse;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.awesome.knowledgechainservice.model.entity.SysConfig;
import com.awesome.knowledgechainservice.service.AnswerInfoService;
import com.awesome.knowledgechainservice.service.KnowledgeBaseInfoService;
import com.awesome.knowledgechainservice.service.KnowledgeInfoService;
import com.awesome.knowledgechainservice.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 82611
 * @description 针对表【knowledge_info】的数据库操作Service实现
 * @createDate 2025-04-09 16:15:38
 */
@Service
public class KnowledgeInfoServiceImpl extends ServiceImpl<KnowledgeInfoMapper, KnowledgeInfo>
        implements KnowledgeInfoService {

    @Resource
    private AnswerInfoService answerInfoService;

    @Resource
    @Lazy
    private KnowledgeBaseInfoService knowledgeBaseInfoService;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public KnowledgeInfo addKnowledgePoint(KnowledgePointInfoDto knowledgePointInfoDto) {
        KnowledgeInfo knowledgeInfo = KnowledgePointInfoDto.transferDB(knowledgePointInfoDto);
        knowledgeInfo.setCreateTime(new Date());
        knowledgeInfo.setUpdateTime(new Date());
        baseMapper.insert(knowledgeInfo);
        return knowledgeInfo;
    }

    @Override
    public void updateLocationList(List<KnowledgePointInfoDto> knowledgePointInfoDtoList) {
        List<KnowledgeInfo> knowledgeInfoList = knowledgePointInfoDtoList.stream().map(k -> {
            KnowledgeInfo knowledgeInfo = KnowledgePointInfoDto.transferDB(k);
            knowledgeInfo.setUpdateTime(new Date());
            return knowledgeInfo;
        }).collect(Collectors.toList());
        baseMapper.updateById(knowledgeInfoList);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除知识点关联的答案
        answerInfoService.deleteByKId(id);
        KnowledgeInfo knowledgeInfo = baseMapper.selectById(id);
        // 删除知识点
        baseMapper.deleteById(id);
        // 如果当前知识库不存在知识点，就删除知识库
        boolean exists = baseMapper.exists(new QueryWrapper<KnowledgeInfo>().lambda()
                .eq(KnowledgeInfo::getKbId, knowledgeInfo.getKbId()));
        if (!exists) {
            knowledgeBaseInfoService.removeById(knowledgeInfo.getKbId());
        }
    }

    @Override
    @Transactional
    public void deleteByKbId(Long id) {
        baseMapper.delete(new QueryWrapper<KnowledgeInfo>().lambda().eq(KnowledgeInfo::getKbId, id));
    }

    private static final String SYSTEM_PROMPT = "" +
            "我会给你一段markdown格式的文本，它描述的是某个领域的某个知识点。" +
            "我需要你根据我发给你的知识点，基于让我充分掌握这个知识的目的，给我出一道主观题。" +
            "你发给我的也应该是纯文本，并且不要多余的修饰，仅包含问题本身即可。" +
            "我希望通过回答这个问题，能够充分掌握这个知识点。";

    private static final SiliconFlowRequest.Message systemMessage = new SiliconFlowRequest.Message()
            .setRole("system")
            .setContent(SYSTEM_PROMPT);

    private SiliconFlowRequest getSiliconFlowRequest() {
        return new SiliconFlowRequest()
                .setModel("deepseek-ai/DeepSeek-V3")
                .setStream(false)
                .setTemperature(0.0)
                .setTopP(1.0)
                .setN(1)
                .setMaxTokens(2048)
                .setResponseFormat(new SiliconFlowRequest.ResponseFormat().setType("text"));
    }


    @Override
    @Transactional
    public String generateQuestionByAI(Long id) {
        KnowledgeInfo knowledgeInfo = baseMapper.selectById(id);
        // 调用AI生成问题 不采用流式输出
        String chatUrl = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_CHAT_URL).one().getSysValue();
        String apiKey = sysConfigService.lambdaQuery().eq(SysConfig::getSysKey, Constants.SILICON_FLOW_API_KEY).one().getSysValue();
        List<SiliconFlowRequest.Message> messages = new ArrayList<>();
        SiliconFlowRequest.Message message = new SiliconFlowRequest.Message()
                .setRole("user")
                .setContent(knowledgeInfo.getMarkdown());
        messages.add(systemMessage);
        messages.add(message);
        SiliconFlowRequest request = getSiliconFlowRequest();
        request.setMessages(messages);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            // 创建请求实体
            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<SiliconFlowResponse> response =
                    restTemplate.exchange(chatUrl, HttpMethod.POST, requestEntity, SiliconFlowResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "硅基流动接口调用异常");
            }
            // 保存成功后返回
            String question = response.getBody().getChoices().get(0).getMessage().getContent();
            knowledgeInfo.setQuestion(question);
            baseMapper.updateById(knowledgeInfo);
            return question;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "硅基流动接口调用异常");
        }
    }
}




