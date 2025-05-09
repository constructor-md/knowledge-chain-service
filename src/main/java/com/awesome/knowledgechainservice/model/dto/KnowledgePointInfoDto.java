package com.awesome.knowledgechainservice.model.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.awesome.knowledgechainservice.model.entity.KnowledgeInfo;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 知识点信息
 */
@Data
public class KnowledgePointInfoDto {

    public String id;
    private String kbId;
    public String title;
    private double x;
    private double y;
    private double z;


    public static KnowledgePointInfoDto transferDto(KnowledgeInfo knowledgeInfo) {
        KnowledgePointInfoDto knowledgePointInfoDto = new KnowledgePointInfoDto();
        knowledgePointInfoDto.setId(String.valueOf(knowledgeInfo.getId()));
        knowledgePointInfoDto.setKbId(String.valueOf(knowledgeInfo.getKbId()));
        knowledgePointInfoDto.setTitle(knowledgeInfo.getTitle());
        if (StrUtil.isNotBlank(knowledgeInfo.getLocation())) {
            List<Double> location = JSONUtil.toList(knowledgeInfo.getLocation(), Double.class);
            if (CollUtil.isNotEmpty(location)) {
                knowledgePointInfoDto.setX(location.get(0));
                knowledgePointInfoDto.setY(location.get(1));
                knowledgePointInfoDto.setZ(location.get(2));
            }
        }
        return knowledgePointInfoDto;
    }

    public static KnowledgeInfo transferDB(KnowledgePointInfoDto knowledgePointInfoDto) {
        KnowledgeInfo knowledgeInfo = new KnowledgeInfo();
        knowledgeInfo.setId(knowledgePointInfoDto.getId() == null ? null : Long.valueOf(knowledgePointInfoDto.getId()));
        knowledgeInfo.setKbId(Long.valueOf(knowledgePointInfoDto.getKbId()));
        knowledgeInfo.setTitle(knowledgePointInfoDto.getTitle());
        List<Double> location = Arrays.asList(knowledgePointInfoDto.getX(), knowledgePointInfoDto.getY(), knowledgePointInfoDto.getZ());
        knowledgeInfo.setLocation(JSONUtil.toJsonPrettyStr(location));
        return knowledgeInfo;
    }

}
