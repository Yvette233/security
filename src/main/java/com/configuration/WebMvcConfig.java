package com.configuration;

import com.util.RequestUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 开启 mvc支持，设置 static 目录为类路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 得到 classpath 的根路径， resources 目录下的所以路径都可以得到
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = (String) RequestUtils.getValueOfProperty("file.upload.dir");
        registry.addResourceHandler("/folder/**") //所有 /file/ 开头的请求都会去后面配置的路径下查找资源
//                .addResourceLocations("classpath:/static/");//相对路径的写法
                .addResourceLocations("file:///" + path);

        // 可以自定义资源处理类，对加载后的资源进行二次处理，比如图片统一打标识、解密之类的
        //.resourceChain(true).addTransformer(new SecretImageResourceTransformerSupport());

    }

}
