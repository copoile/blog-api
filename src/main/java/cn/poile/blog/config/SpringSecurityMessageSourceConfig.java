package cn.poile.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * SpringSecurity异常信息配置
 * @author: yaohw
 * @create: 2019-10-25 15:00
 **/
@Configuration
public class SpringSecurityMessageSourceConfig {
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages");
        return source;
    }
}
