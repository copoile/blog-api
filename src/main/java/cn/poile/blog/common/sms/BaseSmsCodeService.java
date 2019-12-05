package cn.poile.blog.common.sms;

import cn.poile.blog.common.constant.RedisConstant;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: yaohw
 * @create: 2019/11/4 11:20 下午
 */
@Data
public abstract class BaseSmsCodeService implements SmsCodeService, InitializingBean, ApplicationContextAware {

    private StringRedisTemplate redisTemplate;

    private ApplicationContext applicationContext;

    /**
     * 短信验证码有效时间
     */
    private long expire = 300L;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.redisTemplate == null) {
            this.redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        }
        Assert.notNull(this.redisTemplate, "There is no one available StringRedisTemplate bean");
    }

    /**
     * 发送短信验证码，这个提供外部调用的
     * 发送短信成功后缓存短信验证码
     *
     * @param mobile
     * @return
     */
    @Override
    public boolean sendSmsCode(long mobile) {
        SendResult sendResult = handleSendSmsCode(mobile);
        String code = sendResult.getCode();
        boolean smsSuccess = sendResult.isSuccess();
        if (!StringUtils.isBlank(code) && smsSuccess) {
            cacheSmsCode(mobile, code);
            return true;
        }
        return false;
    }

    /**
     * 发送短信验证码实现
     *
     * @param mobile 手机号
     * @return
     */
    protected abstract SendResult handleSendSmsCode(long mobile);

    /**
     * 缓存短信验证码
     *
     * @param mobile
     * @param code
     */
    @Override
    public void cacheSmsCode(long mobile, String code) {
        redisTemplate.opsForValue().set(RedisConstant.SMS_CODE + mobile, code, expire, TimeUnit.SECONDS);
    }

    /**
     * 校验短信验证码
     *
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public boolean checkSmsCode(long mobile, String code) {
        String cacheCode = redisTemplate.opsForValue().get(RedisConstant.SMS_CODE + mobile);
        return !StringUtils.isBlank(cacheCode) && cacheCode.equals(code);
    }

    /**
     * 删除缓存短信验证码
     * @param mobile
     * @return
     */
    @Override
    public boolean deleteSmsCode(long mobile) {
        redisTemplate.delete(RedisConstant.SMS_CODE + mobile);
        return true;
    }

    /**
     * 获取随机6位数验证码
     *
     * @return
     */
    protected String createCode() {
        int random = (int) ((Math.random() * 9 + 1) * 100000);
        return String.valueOf(random);
    }


}
