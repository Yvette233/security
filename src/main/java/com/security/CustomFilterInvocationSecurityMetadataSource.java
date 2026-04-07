package com.security;

import com.manager.SysPermissionManager;
import com.pojo.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/11/24-15:58
 * creator：fangpengcheng
 */
@Service
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    // AntPathMatcher 是一个正则匹配工具
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    private SysPermissionManager sysPermissionManager;

    //根据请求的Url返回所需角色
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //object是一个类似Http Request的对象
        //从object中获取客户端请求的Url，之后从数据库中查询出所有的菜单Url以及哪些角色可以访问对应的菜单Url
        //这是后台方法路径
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
//        String requestUrl = ((FilterInvocation) object).getFullRequestUrl();

        //从数据库中读取
        List<SysPermission> allPermissions = sysPermissionManager.getAllPermissions();
        for (SysPermission permission : allPermissions) {
            //是否可以通过url查找权限？？？
            if (antPathMatcher.match(permission.getUrl(), requestUrl)) {

                String[] roles = permission.getSysRoleSet().stream()
                        .map(role -> "ROLE_" + role.getId()).toArray(String[]::new);
                return SecurityConfig.createList(roles);
            }
        }
        //一些匹配不到的路径，由于目前的项目只有登录页面不需要登录就能访问，而其他Url都是需要登录后才能拿到  因此要创建一个“ROLE_LOGIN”角色来保证Url的权限。
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
