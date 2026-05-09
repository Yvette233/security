package com.core.oauth2.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class ResourceServerConfiguration {

    /**
     * 用 SecurityFilterChain 替代原来的 configure(HttpSecurity http)
     */
    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http
                .antMatcher("/login/getUserInfo") // 仅对该资源接口启用 JWT 验证
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // 配置 OAuth2 资源服务器
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtConverter());

        return http.build();
    }

    /**
     * 将 JWT 中的 authorities 载荷映射为 Spring 权限
     */
    private JwtAuthenticationConverter jwtConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix(""); // 保持 ROLE_ 前缀不被二次加工

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        String jwkSetUri = "http://localhost:8181/oauth2/jwks";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}