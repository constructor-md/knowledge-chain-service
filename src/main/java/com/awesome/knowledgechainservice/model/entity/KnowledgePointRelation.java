package com.awesome.knowledgechainservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName knowledge_point_relation
 */
@TableName(value ="knowledge_point_relation")
@Data
public class KnowledgePointRelation implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 
     */
    @TableField(value = "`left`")
    private Long left;

    /**
     * 
     */
    @TableField(value = "`right`")
    private Long right;

    /**
     * 0 - 不高亮 1- 高亮
     */
    @TableField(value = "highlight")
    private Integer highlight;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}