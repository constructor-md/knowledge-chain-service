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
 * @TableName knowledge_info
 */
@TableName(value ="knowledge_info")
@Data
public class KnowledgeInfo implements Serializable {
    /**
     * 知识点id 雪花
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属知识库id
     */
    @TableField(value = "kb_id")
    private Long kbId;

    /**
     * 知识点title
     */
    @TableField(value = "title")
    private String title;

    /**
     * 知识点坐标  [x, y, z]
     */
    @TableField(value = "location")
    private String location;

    /**
     * 知识点markdown原始文本
     */
    @TableField(value = "markdown")
    private String markdown;

    /**
     * 知识点习题
     */
    @TableField(value = "question")
    private String question;

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