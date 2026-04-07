package com.security.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 在前后端分离项目中，使用 SpringSecurity 后在未登录的情况下，请求后台需要认证后才可以访问的接口，
 * SpringSecurity 会默认重定向到 AuthenticationEntryPoint 接口响应302状态。
 * 而前端无法捕获302，所以我们重新实现响应别的状态码，让前端接收处理。
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 当未认证请求接口，会默认响应302，而前端无法处理，
     * 于是我们就响应一个正常的json字符串，其中code:50008状态，当前端接收到这个后台转到登陆页
     *
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 未认证JSON字符串，
        Result result = Result.build(50008, "请先登录，再访问！");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}