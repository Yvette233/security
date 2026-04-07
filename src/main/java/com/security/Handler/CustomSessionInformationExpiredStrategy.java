package com.security.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.LoginManager;
import com.util.RequestUtils;
import com.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/11/22-16:03
 * creator：fangpengcheng
 */
@Component
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginManager loginManager;

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        Result result = Result.build(50014, "登录超时或已在另一台机器登录，您被迫下线！");
        event.getResponse().setContentType("application/json;charset=UTF-8");
        event.getResponse().getWriter().write(objectMapper.writeValueAsString(result));

        loginManager.deleteToken(RequestUtils.getUserId());
    }

}
