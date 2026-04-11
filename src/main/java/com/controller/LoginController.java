package com.controller;

import com.manager.LoginManager;
import com.pojo.SysLoginInfo;
import com.pojo.SysUser;
import com.util.RequestUtils;
import com.util.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
