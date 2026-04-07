package com.security;

import com.manager.LoginManager;
import com.pojo.SysLoginInfo;
import com.pojo.SysUser;
import com.util.RequestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * 前后端约定登录成功以后，将token放到header中。于是，我们需要过滤器来处理请求Header中的token
 * Copyright (C),Tsinghua university
 * create time：2021/11/22-15:28
 * creator：fangpengcheng
 */
public class TokenFilter extends OncePerRequestFilter {

    //注意过滤器不能采用@Service注入，否则webSecurity.ignoring()的路径失效
    public final static Log log = LogFactory.getLog(TokenFilter.class);

    private LoginManager loginManager = new LoginManager();

    public TokenFilter(LoginManager loginManager) {
        super();
        this.loginManager = loginManager;
    }


    //由于我们采用jwt生成token，因此没法中途更改token的有效期，只能将其放到Redis中，通过更改Redis中key的生存时间来控制token的有效期
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //获取header中的token
        log.info(request.getHeaders(AUTHORIZATION));
        String token = StringUtils.isNotEmpty(request.getHeader(AUTHORIZATION)) ? request.getHeader(AUTHORIZATION) : "";

        Integer loginState = SysLoginInfo.STATE_FAILURE;
        SysUser sysUser = null;
        String loginRoleId = null;
        if (StringUtils.isNotBlank(token) && !"null".equals(token)) {

            //Postman使用Bearer Token时，会自动带上"Bearer "
            if (token.indexOf("Bearer ") == 0) {
                token = token.split("Bearer ")[1];
            }

            //登录用户对象String
            sysUser = loginManager.findByToken(token);
            if (ObjectUtils.isNotEmpty(sysUser) && null == SecurityContextHolder.getContext().getAuthentication()) {
                //登录时候进入if语句
                //构造UsernamePasswordAuthenticationToken放入上下文中。权限可以从数据库中再查一遍，也可以直接从之前的缓存中获取。
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                loginState = SysLoginInfo.STATE_SUCCESS;
                loginRoleId = sysUser.getLoginRoleId();
                //  为token续期，即刷新token
                //  diff单位为毫秒ms
                long diff = (new Date()).getTime() - sysUser.getUpdateTime().getTime();

                //如果生存时间小于10分钟，则登录时间更新为当前时间,要求超时的话重新登陆
                String timeout = (String) RequestUtils.getValueOfProperty("server.servlet.session.timeout");
                if (diff / 1000 < Integer.valueOf(timeout)) {
                    //主要时为了更新访问时间
                    loginManager.updateToken(sysUser.getId(), sysUser.getLoginRoleId(), token);
                } else {
//                    // 这里面未将session清除掉 fpc todo
//                    response.addHeader("type","blob");
//                    response.addHeader("status","50014");
//
//                    loginManager.deleteToken(sysUser.getId());
//
//                    //创建新的session
//                    request.getSession();
//                    RequestDispatcher requestDispatcher = request.getRequestDispatcher(request.getServletPath());
//                    requestDispatcher.forward(request, response);
                }
            }
        }
        loginManager.saveLoginInfo(request, sysUser, null, loginRoleId, loginState, SysLoginInfo.TYPE_ACCESS_PATH);
        chain.doFilter(request, response);
    }


}
