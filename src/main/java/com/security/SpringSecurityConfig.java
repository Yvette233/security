package com.security;

import com.manager.LoginManager;
import com.security.Handler.CustomAccessDeniedHandler;
import com.security.Handler.CustomAuthenticationEntryPoint;
import com.security.Handler.CustomSessionInformationExpiredStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
// 【核心修改 1】：不再继承 WebSecurityConfigurerAdapter
public class SpringSecurityConfig {

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private CustomSessionInformationExpiredStrategy customSessionInformationExpiredStrategy;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    // 如果你有 CustomFilterInvocationSecurityMetadataSource 和 CustomUrlDecisionManager，也在这里 @Autowired 进来

    /**
     * 【核心修改 2】：用 WebSecurityCustomizer 替代原来的 configure(WebSecurity webSecurity)
     * 在这里配置完全忽略，不走 Security 过滤链的路径
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/url/**")
                // 精准放行你手写的两个接口，绝不能用 /login/** 模糊匹配
                .antMatchers("/login/getCode", "/workflow/login/getCode")
                .antMatchers("/login/getToken", "/workflow/login/getToken")
                .antMatchers("/bpmn/**")
                .antMatchers("/table/**")
                .antMatchers("/sysMenu/getMenusByRoleId")
                .antMatchers("/folder/**");
    }

    /**
     * 【核心修改 3】：用 SecurityFilterChain 替代原来的 configure(HttpSecurity http)
     * 在这里配置你的 TokenFilter、跨域、异常处理等
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 关闭 CSRF
                .cors().and()     // 开启跨域
                // 1. 配置异常处理
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()

                // 2. 配置 Session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1) // <--- 注意：这里进入了并发控制子级
                .maxSessionsPreventsLogin(false)
                .expiredSessionStrategy(customSessionInformationExpiredStrategy)
                .and() // <--- 第一次 .and()：退回到 sessionManagement
                .and() // <--- 第二次 .and()：【关键修复】彻底退回到 HttpSecurity 主层级

                // 3. 配置拦截规则
                .authorizeRequests()
                .anyRequest().authenticated();

        // 4. 将你的 JWT 过滤器加到 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(new TokenFilter(loginManager), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 【核心修改 4】：用新架构的方式获取 AuthenticationManager
     * 替代原来的 super.authenticationManagerBean()
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 【保持不变】：密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}