package core.sso.client;

import core.sso.TokenResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OAuth2TokenUtil {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 使用 authorization_code 换取 token
     *
     * @param tokenUrl     授权服务器 token 接口地址
     * @param clientId     客户端ID
     * @param clientSecret 客户端密钥
     * @param code         授权码
     * @param redirectUri  回调地址（必须与获取code时完全一致）
     * @return TokenResponse
     */
    public TokenResponse codeToToken(
            String tokenUrl,
            String clientId,
            String clientSecret,
            String code,
            String redirectUri
    ) {
        // 1. 封装请求体（必须 form 表单格式）
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("grant_type", "authorization_code");
        paramMap.add("code", code);
        paramMap.add("redirect_uri", redirectUri);
        paramMap.add("client_id", clientId);
        paramMap.add("client_secret", clientSecret);

        // 2. 请求头（必须是 FORM 格式）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3. 构造请求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(paramMap, headers);

        try {
            // 4. 发送 POST 请求
            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    TokenResponse.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            // 打印详细错误，方便排查
            System.err.println("Code换Token失败：" + e.getMessage());
            throw new RuntimeException("授权码无效或已过期，请重新授权");
        }
    }

}
