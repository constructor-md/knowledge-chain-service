package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Auth;
import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.dto.KnowledgePointInfoDto;
import com.awesome.knowledgechainservice.model.dto.KnowledgePointSearchDto;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import com.awesome.knowledgechainservice.service.KnowledgeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识点API
 */
@RestController
@RequestMapping("/point")
@Slf4j
public class KnowledgePointController {

    @Resource
    private KnowledgeInfoService knowledgeInfoService;


    /**
     * 查询知识点列表
     */
    @Login
    @GetMapping("/list")
    public R<List<KnowledgePointInfoDto>> list(@RequestParam("id") String id) {
        Long kbId = Long.valueOf(id);
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.lambdaQuery().eq(KnowledgeInfo::getKbId, kbId).list();
        return R.ok(knowledgeInfoList.stream().map(KnowledgePointInfoDto::transferDto).collect(Collectors.toList()));
    }

    /**
     * 新增知识点
     */
    @Login
    @PostMapping("/info")
    @Auth
    public R<KnowledgePointInfoDto> add(@RequestBody KnowledgePointInfoDto knowledgePointInfoDto) {
        KnowledgeInfo knowledgeInfo = knowledgeInfoService.addKnowledgePoint(knowledgePointInfoDto);
        return R.ok(KnowledgePointInfoDto.transferDto(knowledgeInfo));
    }

    /**
     * 编辑指定知识点title
     */
    @Login
    @PutMapping("/title")
    @Auth
    public R<Boolean> updateTitle(@RequestBody KnowledgePointInfoDto knowledgePointInfoDto) {
        knowledgeInfoService.lambdaUpdate().eq(KnowledgeInfo::getId, knowledgePointInfoDto.getId())
                .set(KnowledgeInfo::getTitle, knowledgePointInfoDto.getTitle()).update();
        return R.ok(true);
    }

    /**
     * 批量修改知识点坐标
     */
    @PutMapping("/location")
    @Login
    @Auth
    public R<Boolean> update(@RequestBody List<KnowledgePointInfoDto> knowledgePointInfoDtoList) {
        knowledgeInfoService.updateLocationList(knowledgePointInfoDtoList);
        return R.ok(true);
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/info")
    @Login
    @Auth
    public R<?> delete(@RequestParam("id") String id) {
        Long kId = Long.valueOf(id);
        knowledgeInfoService.delete(kId);
        return R.ok();
    }

    /**
     * 简要信息查询
     * 根据关键词查询相关知识点
     * 每个知识点包含是否与id建立联系的字段
     */
    @GetMapping("/relation/list")
    @Login
    public R<List<KnowledgePointSearchDto>> search(@RequestParam("id") String nowId,
                                                   @RequestParam("keywords") String keywords) {
        return R.ok(knowledgeInfoService.search(Long.valueOf(nowId), keywords));
    }

}
