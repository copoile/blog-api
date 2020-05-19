package cn.poile.blog.common.sms;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信验证码服务自动配置，默认阿里云短信服务
 * @author: yaohw
 * @create: 2019-11-05 10:36
 **/
@Log4j2
@Configuration
@EnableConfigurationProperties({SmsServiceProperties.class})
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SmsCodeService smsService(SmsServiceProperties properties) {
        int type = properties.getType();
        if (type == 1) {
            return new AliSmsCodeService(properties);
        }
        return new TencentSmsCodeService(properties);
    }
}
