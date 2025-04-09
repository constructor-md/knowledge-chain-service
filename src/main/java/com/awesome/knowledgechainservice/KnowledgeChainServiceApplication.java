package com.awesome.knowledgechainservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.awesome.knowledgechainservice.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class KnowledgeChainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeChainServiceApplication.class, args);
    }

}
