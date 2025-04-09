package com.awesome.kuibuservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SYSTEM_ERROR(600500, "系统内部异常"),
    PARAMS_ERROR(600501, "请求参数错误"),
    NOT_LOGIN_ERROR(600502, "未登录"),
    OPERATOR_ERROR(600503, "操作失败"),
    REQUEST_LIMITED(600504, "请求频率限制中"),

    ;

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }






}
