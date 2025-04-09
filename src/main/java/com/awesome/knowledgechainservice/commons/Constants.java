package com.awesome.kuibuservice.commons;

public class Constants {

    //微信登陆URL
    public static final String WX_LOGIN_URL = "WX_LOGIN_URL";
    // 微信小程序APP_ID
    public static final String WX_APP_ID = "WX_APP_ID";
    // 微信小程序APP_SECRET
    public static final String WX_APP_SECRET = "WX_APP_SECRET";

    // TOKEN Redis Key前缀
    public static final String REDIS_ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN::";

    // 硅基流动文本对话URL
    public static final String SILICON_FLOW_CHAT_URL = "SILICON_FLOW_CHAT_URL";
    // 硅基流动API_KEY
    public static final String SILICON_FLOW_API_KEY = "SILICON_FLOW_API_KEY";

    // AI建议缓存前缀
    public static final String REDIS_AI_SUGGESTION_PREFIX = "REDIS_AI_SUGGESTION_PREFIX::";

    // 请求频率限制锁前缀
    public static final String REDIS_REQUEST_LIMITED_PREFIX = "REDIS_REQUEST_LIMITED_PREFIX::";

}
