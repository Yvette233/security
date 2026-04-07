package com.security.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.LoginManager;
import com.util.RequestUtils;
import com.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未授权
 * Copyright (C),Tsinghua university
 * create time：2021/11/22-16:01
 * creator：fangpengcheng
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginManager loginManager;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result result = Result.build(50016, "抱歉，您没有访问方法" + request.getRequestURL() + "的权限！");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));

        loginManager.deleteToken(RequestUtils.getUserId());
    }

}
