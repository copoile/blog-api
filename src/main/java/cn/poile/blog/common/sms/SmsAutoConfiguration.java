package cn.poile.blog.common.sms;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yaohw
 * @create: 2019-11-05 10:36
 **/
@Configuration
@EnableConfigurationProperties({SmsServiceProperties.class})
@Log4j2
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SmsService smsService(SmsServiceProperties properties) {
        return new AliSmsService(properties);
    }
}
