package com.controller;

import com.core.sso.client.OAuth2TokenUtil;
import com.manager.*;
import com.pojo.SysLoginInfo;
import com.pojo.SysUser;
import com.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.core.sso.TokenResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.security.oauth2.jwt.Jwt;


/**
 * Copyright (C),Tsinghua university
 * create time：2021/6/30-20:59
 * creator：fangpengcheng
 */

@RestController
@RequestMapping("/login")
public class LoginController {

    public final static Log log = LogFactory.getLog(LoginController.class);

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SysUserManager sysUserManager;
    @Autowired
    private SysDeptManager sysDeptManager;
    @Autowired
    private SysRoleManager sysRoleManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IdpIntegrationManager idpIntegrationManager;

    @Autowired
    private PublicManager publicManager;

    @Autowired
    private OAuth2TokenUtil oAuth2TokenUtil; // 引入用于向内部发请求换取标准JWT的工具[cite: 8]

    @Autowired
    private StringRedisTemplate redisTemplate; // 引入 Redis 进行高可用存储

    @Value("${idp.client-id:messaging-client}")
    private String defaultClientId;

    @Value("${idp.client-secret:secret}")
    private String defaultClientSecret;

    // 定义 Redis 缓存 Key 前缀
    private static final String AUTH_CODE_PREFIX = "OAUTH_CODE:";


//    @ApiModelProperty(value = "返回类型", dataType = "String")
    private String warnLogs;
    /*******************************************登录登出*****************************************************/

    /**
     * This controller is responsible to return a token for valid credentials:
     *
     * @param user
     * @param password
     * @return
     */
    @PostMapping("/getToken")
    @ResponseBody
    public Result getToken(@RequestParam("user") final String user, @RequestParam("password") final String password) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, password);
        Authentication authentication = null;
        try {
            //调用获取数据库sysUser,同时进行认证authenticated,包括前DaoAuthenticationProvider的76行中对密码进行matches
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            loginManager.saveLoginInfo(RequestUtils.getRequest(), null, user, null, SysLoginInfo.TYPE_LOGIN, SysLoginInfo.STATE_FAILURE);
            return Result.error(e.getMessage() + "用户或密码错误");
        }

        SysUser userDetails = loginManager.updateSysUserAtLogin(user, authentication);

        return Result.ok(userDetails);
    }


    /**
     * 1. 验证身份并申请临时 Auth Code
     * 接口路径：/login/getAuthCode
     */
    @PostMapping("/getCode")
    public Result getCode(@RequestParam("client_id") String client_id,
                                 @RequestParam("client_secret") String client_secret,
                                 @RequestParam("username") String username,
                                 @RequestParam("password") String password) {

        // 参数校验
        if (StringUtils.isBlank(client_id)) return Result.error("1001");
        if (StringUtils.isBlank(client_secret)) return Result.error("1008");
        //if (!"aps_client".equals(client_id)) return Result.error("1005");
        if (StringUtils.isBlank(username)) return Result.error("1015");
        if (StringUtils.isBlank(password)) return Result.error("1016");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = null;
        try {
            //调用获取数据库sysUser,同时进行认证authenticated,包括前DaoAuthenticationProvider的76行中对密码进行matches
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            loginManager.saveLoginInfo(RequestUtils.getRequest(), null, username, null, SysLoginInfo.TYPE_LOGIN, SysLoginInfo.STATE_FAILURE);
            return Result.error(e.getMessage() + "用户或密码错误");
        }

        SysUser userDetails = loginManager.updateSysUserAtLogin(username, authentication);

        // 校验通过，生成高强度无规律的授权码 Code
        String code = "AUTH_CODE_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();

        // 将 code 存入 Redis，有效期设为 5 分钟（真实生产环境防重放设计）
        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + code, username, 5, TimeUnit.MINUTES);

        // 成功返回（严格遵循文档 Result 结构）
        return Result.ok("操作成功", code);
    }

    /**
     * 3. 获取当前登录用户的全量业务数据及权限菜单
     * 接口路径：/login/getUserInfo
     */
    @GetMapping("/getUserInfo")
    public Result getUserInfo(@AuthenticationPrincipal Jwt jwt,
                                 @RequestParam(required = false) String client_id,
                                 @RequestParam(required = false) String client_secret,
                                 @RequestParam(required = false) String username) {

        // 1. 安全校验：确保 Token 主体与传入的 username 一致
        if (jwt == null || !username.equals(jwt.getSubject())) {
            return Result.error("500");
        }

        // 2. 获取业务数据
        SysUser sysUser = sysUserManager.getById(username);
        if (sysUser == null) {
            return Result.error("500");
        }

        // --- 拼接 responseBody ---
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", sysUser.getId());
        userInfo.put("username", sysUser.getUsername());
        userInfo.put("realname", sysUser.getUserNameCH());
        //userInfo.put("avatar", sysUser.getAvatar() != null ? sysUser.getAvatar() : "/api/appsys/user/user/photo/get");
        userInfo.put("currentAppId", "APP001");
        userInfo.put("currentLanguageCode", "zh-CN");
        userInfo.put("secretLevelName", "秘密级");

        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userInfo);
        data.put("token", jwt.getTokenValue());

        return Result.ok("操作成功", data);
    }


    @DeleteMapping("/deleteToken")
    public Result deleteToken(@RequestParam String user) {
        return loginManager.deleteToken(user);
    }

    /********************************************获取当前用户信息****************************************************/


    @RequestMapping(value = "/getCurrentUser")
    @ResponseBody
    public Object getCurrentUser() {
        return Result.ok(RequestUtils.getUsername());
    }

    @PutMapping(value = "/updateLoginRoleId")
    public Result updateLoginRoleId(@RequestParam String userName, @RequestParam String loginRoleId) {
        loginManager.updateLoginRoleId(userName, loginRoleId);
        return Result.ok("切换成功");
    }

}
