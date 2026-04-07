package com.security;

import com.manager.LoginManager;
import com.security.Handler.CustomAccessDeniedHandler;
import com.security.Handler.CustomAuthenticationEntryPoint;
import com.security.Handler.CustomSessionInformationExpiredStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity // 开启 springsecurity 认证配置
@EnableGlobalMethodSecurity(prePostEnabled = true)//启用方法安全设置
//@EnableGlobalMethodSecurity(prePostEnabled = false)//启用方法安全设置
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    public final static Log log = LogFactory.getLog(SpringSecurityConfig.class);


    @Resource
    private CustomUserDetailsService customUserDetailsService;

    @Resource
    private LoginManager loginManager;

    @Resource
    private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;

    @Resource
    private CustomUrlDecisionManager customUrlDecisionManager;

    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Resource
    private CustomSessionInformationExpiredStrategy customSessionInformationExpiredStrategy;

    @Resource
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /*******************************************1、认证*******************************************************/


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.setAllowedHeaders(Arrays.asList("*", AUTHORIZATION));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 认证管理器，用于配置全局的认证相关的信息。
     * UserDetailsService用户详情查询服务。
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //认证信息
        auth.userDetailsService(customUserDetailsService);

        //不删除凭据，以便记住用户
        //auth.eraseCredentials(false);
    }


    /**
     * Authentication，认证服务提供者
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    /******************************************* 2、进行资源权限控制规则相关配置*******************************************************/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        //基于URL地址的权限管理
        http.authorizeRequests()
                .anyRequest().authenticated()//所有请求都得被security认证
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <Object extends FilterSecurityInterceptor> Object postProcess(Object object) {
                        object.setAccessDecisionManager(customUrlDecisionManager);
                        object.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        //所有的URL地址都必须在数据库中配置URL--权限映射关系后才能访问。
                        object.setRejectPublicInvocations(true);
                        return object;
                    }
                });

        //超期处理等
        http
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .csrf().disable()
                .cors(withDefaults())//一定不要忘
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)// No session will be created or used by spring security
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredSessionStrategy(customSessionInformationExpiredStrategy);

        http.addFilterBefore(new TokenFilter(loginManager), UsernamePasswordAuthenticationFilter.class);
    }


    /*********************************************3、进行全局请求忽略规则配置、HttpFirewall配置、Debug配置、全局SecurityFilterChain配置。**********************************************************/

    /**
     * 外部过滤
     * 注意：下面路径下所有方法，不能通过SecurityContextHolder.getContext()...获取userId/userName
     */
    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        //不拦截静态资源,
        webSecurity.ignoring().antMatchers("/url/**")//window.open的url暂时权限放开
                .antMatchers("/login/**")//访问： 无需登录认证权限
                .antMatchers("/bpmn/**")
                .antMatchers("/table/**")
                .antMatchers("/sysMenu/getMenusByRoleId")
                .antMatchers("/folder/**");
    }

    /************************************************4、加密处理*******************************************************/


    /**
     * 手动在拦截器中配置注册一个单例的bean对象，避免每次都重新生成
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 明文+随机盐值》加密存储
        return new BCryptPasswordEncoder();
    }


}
