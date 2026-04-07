package com.util;

import com.pojo.SysUser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequestUtils {

    public static Object getValueOfProperty(String name) {
        Resource resource = new ClassPathResource("application.properties");
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            return props.getProperty(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        System.out.println(request.getRequestURL() + request.getRequestURI());
        System.out.println(request.getLocalAddr() + "\\" + request.getRemoteHost());
        return request;
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        return response;
    }

    /**
     * 获取登录用户名
     *
     * @return
     */
    public static String getUserId() {
        //getPrincipal()方法返回身份信息，是UserDetails对身份信息的封装
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((SysUser) principal).getId();
    }

    /**
     * 获取登录用户名
     *
     * @return
     */
    public static String getUsername() {
        //getPrincipal()方法返回身份信息，是UserDetails对身份信息的封装

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        if (principal instanceof Principal) {
            return ((Principal) principal).getName();
        }
        return String.valueOf(principal);
    }

    public static String getCurrentUsername() {
        //getAuthentication()方法返回认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            Set<String> set = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            return set.contains(permission);

//            //  AuthorityUtils.createAuthorityList(permission);
//            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(permission);
//            return userDetails.getAuthorities().contains(simpleGrantedAuthority);
        }
        return false;
    }
}
