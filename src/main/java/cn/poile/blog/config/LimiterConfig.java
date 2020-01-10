package cn.poile.blog.config;

import cn.poile.blog.common.limiter.ExtraLimiter;
import cn.poile.blog.common.limiter.SmsLimiter;
import cn.poile.blog.common.sms.SmsServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: yaohw
 * @create: 2019-11-23 11:17
 **/
@Component
public class LimiterConfig {


    @Bean("smsLimiter")
    public ExtraLimiter limiter(SmsServiceProperties properties) {
        SmsLimiter smsLimiter = new SmsLimiter();
        smsLimiter.setDayMax(properties.getDayMax());
        return smsLimiter;
    }
}
