package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Auth;
import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.dto.util.UndirectedConnection;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.awesome.knowledgechainservice.service.KnowledgeInfoService;
import com.awesome.knowledgechainservice.service.KnowledgePointRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识点关系API
 */
@RestController
@RequestMapping("/relation")
@Slf4j
public class KnowledgeRelationController {

    @Resource
    private KnowledgeInfoService knowledgeInfoService;

    @Resource
    private KnowledgePointRelationService knowledgeRelationService;


    /**
     * 查询知识库内所有知识点关系
     */
    @GetMapping("/all")
    @Login
    public R<List<String[]>> getAllRelationForKB(@RequestParam("id") String id) {
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService
                .lambdaQuery()
                .eq(KnowledgeInfo::getKbId, id)
                .list();
        List<Long> kIdList = knowledgeInfoList.stream()
                .map(KnowledgeInfo::getId)
                .distinct().collect(Collectors.toList());
        return R.ok(knowledgeRelationService.findAllRelationsBetweenNodes(kIdList));
    }

    /**
     * 建立关系
     */
    @PostMapping("/info")
    @Login
    @Auth
    public R<Boolean> addRelation(@RequestParam("sourceId") String sourceId,
                                  @RequestParam("targetId") String targetId) {
        knowledgeRelationService.createRelation(UndirectedConnection.create(Long.valueOf(sourceId), Long.valueOf(targetId)));
        return R.ok(true);
    }

    /**
     * 删除关系
     */
    @DeleteMapping("/info")
    @Login
    @Auth
    public R<Boolean> deleteRelation(@RequestParam("sourceId") String sourceId,
                                     @RequestParam("targetId") String targetId) {
        knowledgeRelationService.deleteRelation(UndirectedConnection.create(Long.valueOf(sourceId), Long.valueOf(targetId)));
        return R.ok(true);
    }


}
