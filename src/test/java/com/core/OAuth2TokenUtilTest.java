package com.core;

import core.sso.client.OAuth2TokenUtil;
import core.sso.TokenResponse;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class OAuth2TokenUtilTest {

    @Resource
    private OAuth2TokenUtil oAuth2TokenUtil;

    @Test
    public void test() {
        // 1. 配置信息（建议写到 application.yml）
        String tokenUrl = "https://授权服务器.com/oauth2/token";
        String clientId = "你的clientId";
        String clientSecret = "你的clientSecret";
        String redirectUri = "https://你的回调地址.com/callback"; // 必须完全一致
        String code = "前端传过来的code";

        // 2. 一行调用
        TokenResponse token = oAuth2TokenUtil.codeToToken(
                tokenUrl, clientId, clientSecret, code, redirectUri
        );

        // 3. 获取结果
        System.out.println("access_token = " + token.getAccess_token());
        System.out.println("refresh_token = " + token.getRefresh_token());
    }
}
