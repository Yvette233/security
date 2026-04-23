package com.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// 👇 @Configuration 告诉 Spring 这是一个配置类，启动时要来这里读取
@Configuration
public class RestTemplateConfig {

    // 👇 @Bean 告诉 Spring 把这个方法的返回值放到容器库里去
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}