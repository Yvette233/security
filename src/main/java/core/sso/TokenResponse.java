package core.sso;

/**
 * Token 响应实体（标准 OAuth2 格式）
 */
public class TokenResponse {

    private String access_token;   // 接口调用凭证
    private String token_type;     // Bearer
    private Long expires_in;       // 过期时间（秒）
    private String refresh_token;  // 刷新token
    private String scope;          // 权限范围
    private String id_token;       // OIDC 用户信息（可选）

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
}
