package com.awesome.knowledgechainservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName knowledge_base_info
 */
@TableName(value ="knowledge_base_info")
@Data
public class KnowledgeBaseInfo implements Serializable {
    /**
     * 知识库id 雪花
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 知识库名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}