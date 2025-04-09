package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.aop.UserInfoContext;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.LoginRequest;
import com.awesome.knowledgechainservice.model.entity.UserInfo;
import com.awesome.knowledgechainservice.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    @Resource
    private UserInfoService userInfoService;


    @PostMapping("/register")
    public R<?> register(@RequestBody LoginRequest registerRequest) {
        userInfoService.register(registerRequest);
        return R.ok();
    }


    @PostMapping("/login")
    public R<?> login(@RequestBody LoginRequest loginRequest) {
        return R.ok(userInfoService.login(loginRequest));
    }


    @GetMapping("/auth")
    @Login
    public R<?> auth() {
        UserInfo userInfo = UserInfoContext.get();
        log.info("user: {}", userInfo.getUsername());
        return R.ok();
    }





}
