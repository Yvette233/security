package com.controller;

import com.manager.IdpIntegrationManager;
import com.manager.LoginManager;
import com.manager.PublicManager;
import com.pojo.SysLoginInfo;
import com.pojo.SysUser;
import com.util.RequestUtils;
import com.util.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
/**/
    @Autowired
    private IdpIntegrationManager idpIntegrationManager;

    @Autowired
    private PublicManager publicManager;

    @Value("${idp.client-id}")
    private String defaultClientId;

    @Value("${idp.client-secret}")
    private String defaultClientSecret;

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
//    @PostMapping("/getToken")
//    @ResponseBody
//    public Result getToken(@RequestParam("user") final String user, @RequestParam("password") final String password) {
//
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, password);
//        Authentication authentication = null;
//        try {
//            //调用获取数据库sysUser,同时进行认证authenticated,包括前DaoAuthenticationProvider的76行中对密码进行matches
//            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
//        } catch (Exception e) {
//            loginManager.saveLoginInfo(RequestUtils.getRequest(), null, user, null, SysLoginInfo.TYPE_LOGIN, SysLoginInfo.STATE_FAILURE);
//            return Result.error(e.getMessage() + "用户或密码错误");
//        }
//
//        SysUser userDetails = loginManager.updateSysUserAtLogin(user, authentication);
//
//        return Result.ok(userDetails);
//    }

    /**
     * 接口一：获取 Token 接口
     * 实现逻辑：接收前端 code，通过后端换取中台的 access_token
     */
    @PostMapping("/getToken")
    public Result getToken(
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam("code") String code,
            @RequestParam(value = "grant_type", defaultValue = "authorization_code") String grantType) {

        clientId = (clientId == null) ? defaultClientId : clientId;
        clientSecret = (clientSecret == null) ? defaultClientSecret : clientSecret;

        // 调用融合后的 Manager 方法请求中台
        Map<String, Object> result = idpIntegrationManager.getTokenFromIdp(clientId, clientSecret, code, grantType);

        // 校验中台返回的错误码
        if (result != null && result.containsKey("errcode")) {
            return Result.error(result.get("msg").toString());
        }

        return Result.ok(result);
    }

    /**
     * 接口二：获取用户信息接口
     * 实现逻辑：利用 access_token 获取身份，并完成 APS 系统本地权限装载
     */
    @GetMapping("/getUserInfo")
    public Result getUserInfo(
            @RequestParam("access_token") String accessToken,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam("uid") String uid) {

        clientId = (clientId == null) ? defaultClientId : clientId;

        // 向中台请求用户信息
        Map<String, Object> userInfo = idpIntegrationManager.getUserInfoFromIdp(accessToken, clientId, uid);

        // 校验中台异常
        if (userInfo != null && userInfo.containsKey("errcode")) {
            return Result.error(userInfo.get("msg").toString());
        }

        // ================= 融合业务系统登录逻辑 =================
        // 文档规定：spRoleList 值为应用系统的账号名
        List<String> spRoleList = (List<String>) userInfo.get("spRoleList");
        if (spRoleList == null || spRoleList.isEmpty()) {
            return Result.error("该用户未绑定 APS 系统账号");
        }

        String localUsername = spRoleList.get(0);

        // 调用项目中原有的 PublicManager 查找本地用户记录
        SysUser sysUser = publicManager.findSysUser(localUsername);
        if (sysUser == null) {
            return Result.error("本地系统不存在该账号，请联系管理员同步");
        }

        // 执行本地登录流程：生成 JWT 并装载级联权限树（如 sysRoleSet, sysDeptSet 等）
        Authentication auth = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
        SysUser userWithApsToken = loginManager.updateSysUserAtLogin(localUsername, auth);

        // 将中台信息与本地生成的 Token 融合返回
        userInfo.put("aps_token", userWithApsToken.getToken());
        userInfo.put("aps_user_details", userWithApsToken);

        return Result.ok(userInfo);
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
