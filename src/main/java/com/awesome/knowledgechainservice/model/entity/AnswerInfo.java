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
 * @TableName answer_info
 */
@TableName(value ="answer_info")
@Data
public class AnswerInfo implements Serializable {
    /**
     * 回答id 雪花
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属知识点id
     */
    @TableField(value = "k_id")
    private Long kId;

    /**
     * 回答所属用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 用户回答文本
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 对用户回答的评价
     */
    @TableField(value = "evaluation")
    private String evaluation;

    /**
     * 对用户回答的评分 百分制
     */
    @TableField(value = "score")
    private Integer score;

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