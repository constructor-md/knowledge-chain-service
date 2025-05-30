package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.commons.R;
import com.awesome.knowledgechainservice.model.LoginRequest;
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


    /**
     * 返回是否具备知识库编辑权限
     * 目前仅admin用户可修改
     */
    @GetMapping("/auth")
    @Login
    public R<Boolean> auth() {
        return R.ok(userInfoService.isAdmin());
    }





}
