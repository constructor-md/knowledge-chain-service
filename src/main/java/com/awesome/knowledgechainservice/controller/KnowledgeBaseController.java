package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Auth;
import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.dto.KnowledgeBaseInfoDto;
import com.awesome.knowledgechainservice.model.entity.KnowledgeBaseInfo;
import com.awesome.knowledgechainservice.service.KnowledgeBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库API
 */
@RestController
@RequestMapping("/base")
@Slf4j
public class KnowledgeBaseController {

    @Resource
    private KnowledgeBaseInfoService knowledgeBaseInfoService;


    /**
     * 获取知识库列表
     */
    @Login
    @GetMapping("/list")
    public R<List<KnowledgeBaseInfoDto>> list() {
        List<KnowledgeBaseInfo> knowledgeBaseInfoList = knowledgeBaseInfoService.list();
        return R.ok(knowledgeBaseInfoList.stream().map(KnowledgeBaseInfoDto::transferDto).collect(Collectors.toList()));
    }


    /**
     * 新增知识库
     */
    @PostMapping("/info")
    @Login
    @Auth
    public R<String> addKB(@RequestParam("name") String name) {
        KnowledgeBaseInfo knowledgeBaseInfo = knowledgeBaseInfoService.addKnowledgeBaseInfo(name);
        return R.ok(String.valueOf(knowledgeBaseInfo.getId()));
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/info")
    @Login
    @Auth
    public R<Boolean> deleteKB(@RequestParam("id") String id) {
        return R.ok(knowledgeBaseInfoService.deleteKnowledgeBaseInfo(Long.valueOf(id)));
    }

}
