package com.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份中台集成管理类
 * 负责与外部 Oauth2 认证中心进行通信
 */
@Service
public class IdpIntegrationManager {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${idp.base-url}")
    private String idpBaseUrl;

    /**
     * 对应文档 2.2.2：向中台获取 access_token
     */
    public Map<String, Object> getTokenFromIdp(String clientId, String clientSecret, String code, String grantType) {
        String url = idpBaseUrl + "/idp/oauth2/getToken?client_id={clientId}&client_secret={clientSecret}&code={code}&grant_type={grantType}";

        Map<String, String> vars = new HashMap<>();
        vars.put("clientId", clientId);
        vars.put("clientSecret", clientSecret);
        vars.put("code", code);
        vars.put("grantType", grantType);

        // 使用 POST 请求调用中台
        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class, vars);
        return (Map<String, Object>) response.getBody();
    }

    /**
     * 对应文档 2.2.3：向中台获取详细用户信息
     */
    public Map<String, Object> getUserInfoFromIdp(String accessToken, String clientId, String uid) {
        String url = idpBaseUrl + "/idp/oauth2/getUserInfo?access_token={accessToken}&client_id={clientId}&uid={uid}";

        Map<String, String> vars = new HashMap<>();
        vars.put("accessToken", accessToken);
        vars.put("clientId", clientId);
        vars.put("uid", uid);

        // 使用 GET 请求调用中台
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, vars);
        return (Map<String, Object>) response.getBody();
    }
}