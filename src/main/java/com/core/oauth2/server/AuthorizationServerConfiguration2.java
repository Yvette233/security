package com.core.oauth2.server;

import com.controller.LoginController;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import oracle.security.crypto.core.RSAPrivateKey;
import oracle.security.crypto.core.RSAPublicKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.UUID;

public class AuthorizationServerConfiguration2 {

    public final static Log log = LogFactory.getLog(LoginController.class);

    /**
     * 配置 oauth2 相关接口的拦截执行链， 具体的接口配置在 AuthorizationServerSettings 中。
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
// 这里配置了当前 FilterChain 只会拦截oauth2相关的请求
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationEndpoint(oAuth2AuthorizationEndpointConfigurer -> {
                    oAuth2AuthorizationEndpointConfigurer.authenticationProviders(authenticationProviders -> {
// 配置授权码生成策略
                        for (AuthenticationProvider authenticationProvider : authenticationProviders) {
                            log.info(" consumer authenticationProvider , current -> {} " + authenticationProvider.getClass().getName());
                            if (authenticationProvider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider) {
                                OAuth2AuthorizationCodeRequestAuthenticationProvider oAuth2AuthorizationCodeRequestAuthenticationProvider = (OAuth2AuthorizationCodeRequestAuthenticationProvider) authenticationProvider;
                                oAuth2AuthorizationCodeRequestAuthenticationProvider.setAuthorizationCodeGenerator(authorizationCodeGenerator());
                            } else if (authenticationProvider instanceof OAuth2AuthorizationConsentAuthenticationProvider) {
                                OAuth2AuthorizationConsentAuthenticationProvider oAuth2AuthorizationConsentAuthenticationProvider = (OAuth2AuthorizationConsentAuthenticationProvider) authenticationProvider;
                                oAuth2AuthorizationConsentAuthenticationProvider.setAuthorizationCodeGenerator(authorizationCodeGenerator());
                            }
                        }
                    });
                })
                .authorizationService(new InMemoryOAuth2AuthorizationService()) // 认证后token存储配置，默认在内存，可配置Redis与DB
                .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
        http
// Redirect to the login page when not authenticated from the
// authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                )
// 使用 access_token 的方式
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        ;
        return http.build();
    }

    // 这里是配置一个内存 Client
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret(new BCryptPasswordEncoder().encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://127.0.0.1:8080/authorized")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    // 配置数据库查询的 Client
    @Bean
    public RegisteredClientRepository registeredClientRepository(@Qualifier("customJdbcTemplate") JdbcTemplate jdbcTemplate) {

        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    // 配置JWT的生成方式
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    // 配置JWT的生成方式
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * 配置自定义的授权码 authorization_code，默认实现：OAuth2AuthorizationCodeGenerator
     * - 下面简单配置成为了 UUID 的形式。
     */
    public OAuth2TokenGenerator<OAuth2AuthorizationCode> authorizationCodeGenerator() {
        return context -> {
            log.info(" use custom authorization_code creator ");
            if (context.getTokenType() == null ||
                    !OAuth2ParameterNames.CODE.equals(context.getTokenType().getValue())) {
                return null;
            }
            String authorizationCode = UUID.randomUUID().toString().replaceAll("-", "");
            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plus(context.getRegisteredClient().getTokenSettings().getAuthorizationCodeTimeToLive());
            return new OAuth2AuthorizationCode(authorizationCode, issuedAt, expiresAt);
        };
    }

}
