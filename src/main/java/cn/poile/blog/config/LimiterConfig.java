package cn.poile.blog.config;

import cn.poile.blog.common.limiter.AdditionalLimiter;
import cn.poile.blog.common.limiter.SmsLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: yaohw
 * @create: 2019-11-23 11:17
 **/
@Component
public class LimiterConfig {

    @Bean("smsLimiter")
    public AdditionalLimiter limiter() {
        return new SmsLimiter();
    }
}
