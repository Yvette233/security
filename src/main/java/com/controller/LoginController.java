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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Instant;
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

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private OAuth2AuthorizationService authorizationService;


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
    @PostMapping(value = "/getCode")
    public Result getCode(@RequestParam("client_id") String clientId,
                          @RequestParam("client_secret") String client_secret,
                          @RequestParam("username") String username,
                          @RequestParam("password") String password) {

        // 1. 拦截前端密码，进行底层校验
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authRequest);
        } catch (Exception e) {
            return Result.error("账号或密码错误");
        }

        // 2. 校验客户端凭证是否合法
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null) {
            return Result.error("无效的客户端 ID");
        }

        // 3. 核心：接管 SAS 的发证逻辑，生成授权码
        String codeValue = "AUTH_CODE_" + UUID.randomUUID().toString().replaceAll("-", "");
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(300); // 5分钟有效期
        OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(codeValue, issuedAt, expiresAt);

        // 4. 伪造一个完整的授权请求对象
        String redirectUri = "http://127.0.0.1:8181/login/oauth2/code/aps_client";
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scopes(client.getScopes())
                .state("state_" + UUID.randomUUID().toString().substring(0, 6)) // 伪造防篡改随机码
                .authorizationUri("http://localhost:8181/workflow/oauth2/authorize")
                .build();

        // 4. 将手动生成的 Code 打包成 SAS 认可的上下文，并存入其账本
        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(client)
                .principalName(authentication.getName())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .attribute(Principal.class.getName(), authentication) // 极度关键：SAS 换 Token 时靠它提取用户权限
                .attribute(OAuth2AuthorizationRequest.class.getName(), authorizationRequest)
                // 告诉 SAS 这个请求不走 PKCE 校验，防止底层 NullPointerException
                .attribute("org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationToken.PKCE_ON", false)
                                .authorizedScopes(client.getScopes())
                // 注意：由于是内部强行发证，强行指定一个回调地址防伪。后续换 Token 时必须传这个一模一样的值
                .attribute("redirect_uri", "http://127.0.0.1:8181/login/oauth2/code/aps_client")
                .token(authorizationCode)
                .build();

        authorizationService.save(authorization);

        // 将 code 存入 Redis，有效期设为 5 分钟
        redisTemplate.opsForValue().set(codeValue, username, 5, TimeUnit.MINUTES);

        // 6. 返回优雅的格式！
        return Result.ok("操作成功", codeValue);
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
