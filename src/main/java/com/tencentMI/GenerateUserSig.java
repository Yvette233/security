package com.tencentMI;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GenerateUserSig {
    /**
     * 签名过期时间，建议不要设置的过短
     * <p>
     * 时间单位：秒
     * 默认时间：7 x 24 x 60 x 60 = 604800 = 7 天
     */
    @Value("${IMConfig.expireTime}")
    public long expireTime;
    /**
     * 计算签名用的加密密钥，获取步骤如下：
     * <p>
     * step1. 进入腾讯云实时音视频[控制台](https://console.cloud.tencent.com/rav )，如果还没有应用就创建一个，
     * step2. 单击“应用配置”进入基础配置页面，并进一步找到“帐号体系集成”部分。
     * step3. 点击“查看密钥”按钮，就可以看到计算 UserSig 使用的加密的密钥了，请将其拷贝并复制到如下的变量中
     * <p>
     * 注意：该方案仅适用于调试Demo，正式上线前请将 UserSig 计算代码和密钥迁移到您的后台服务器上，以避免加密密钥泄露导致的流量盗用。
     * 文档：https://cloud.tencent.com/document/product/647/17275#Server
     */
    @Value("${IMConfig.secretKey}")
    public String secretKey;
    /**
     * 腾讯云 SDKAppId，需要替换为您自己账号下的 SDKAppId。
     * <p>
     * 进入腾讯云实时音视频[控制台](https://console.cloud.tencent.com/rav ) 创建应用，即可看到 SDKAppId，
     * 它是腾讯云用于区分客户的唯一标识。
     */
    @Value("${IMConfig.sdkAppId}")
    private long sdkAppId;

    public Pair<Long, String> generateUserSig(String userId) {

        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        return Pair.of(sdkAppId, api.genUserSig(userId, expireTime));
    }


}
