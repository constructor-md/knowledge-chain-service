package com.awesome.knowledgechainservice.controller;

import com.awesome.knowledgechainservice.annotation.Auth;
import com.awesome.knowledgechainservice.annotation.Login;
import com.awesome.knowledgechainservice.commons.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
@Slf4j
public class TestController {


    @GetMapping("/test")
    @Login
    @Auth
    public R<?> test() {
        return R.ok();
    }


}
