package com.awesome.knowledgechainservice.aop;

import com.awesome.knowledgechainservice.exception.BusinessException;
import com.awesome.knowledgechainservice.exception.ErrorCode;
import com.awesome.knowledgechainservice.exception.ThrowUtils;
import com.awesome.knowledgechainservice.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@Order(value = 2)
@Slf4j
public class AuthAspect {

    @Resource
    private UserInfoService userInfoService;

    @Pointcut(value = "@annotation(com.awesome.knowledgechainservice.annotation.Auth))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void requestLimited() {
        boolean isAdmin = userInfoService.isAdmin();
        System.out.println(isAdmin);
        ThrowUtils.throwIf(!isAdmin, new BusinessException(ErrorCode.FORBIDDEN_ERROR));
    }

}
