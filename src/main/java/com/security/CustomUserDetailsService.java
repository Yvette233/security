package com.security;

import com.manager.LoginManager;
import com.manager.PublicManager;
import com.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private LoginManager loginManager;
    @Resource
    private PublicManager publicManager;
    @Autowired
    private HttpServletRequest request;

    //登陆时调用
    //用于根据用户名从数据库、redis或内存中加载相关用户信息，包含了用户名、密码、权限、是否启用、是否被锁定、是否过期等
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //可以设置黑名单ip
        final String ip = getClientIP();

//        if (loginAttemptService.isBlocked(ip)) {
//            throw new RuntimeException("blocked");
//        }

        SysUser sysUser = this.publicManager.findSysUser(username);
        // SecurityUser实现UserDetails并将SUser的Email映射为username
        if (sysUser == null) {
            throw new UsernameNotFoundException(username + "用户名、邮箱或手机号不存在！");
        } else if (sysUser.getState() == 0) {
            throw new LockedException("用户被锁定，无法登录");
        }

        loginManager.setSimpleGrantedAuthority(sysUser);


        //activiti中调用taskRuntime.task(taskId)，需要先通过通过Principal::getName获取登录用户是否属于分派任务得候选人或候选组？
        //fpc todo 可以改为taskService...
//        sysUser.setName(sysUser.getId());

        return sysUser;
    }


    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }


}