package cn.poile.blog.common.sms;

import cn.poile.blog.common.constant.RedisConstant;
import com.aliyuncs.CommonRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: yaohw
 * @create: 2019/11/4 11:20 下午
 */
public abstract class BaseSmsService implements SmsService, InitializingBean {

    private StringRedisTemplate redisTemplate;

    /**
     * 短信验证码有效时间
     */
    private long expire = 300L;

    @Override
    public void afterPropertiesSet() {

    }

    /**
     * 发送短信验证码，这个提供外部调用的
     * @param mobile
     * @return
     */
    @Override
    public boolean sendSmsCode(long mobile) {
        Map<String,Boolean> resultMap = handleSendSmsCode(mobile);
        Set<Map.Entry<String, Boolean>> entrySet = resultMap.entrySet();
        Iterator<Map.Entry<String, Boolean>> iterator = entrySet.iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, Boolean> item = iterator.next();
            String code = item.getKey();
            Boolean smsSuccess = item.getValue();
            if (!StringUtils.isEmpty(code) && smsSuccess) {
                cacheSmsCode(mobile,code);
                return true;
            }
        }
        return false;
    }

    /**
     * 发送短信验证码实现
     * @param mobile 手机号
     * @return 返回要求为一个key为验证码，value为短信是否发送成功的一个map
     */
    protected abstract Map<String,Boolean> handleSendSmsCode(long mobile);

    /**
     * 缓存短信验证码
     * @param mobile
     * @param code
     */
    @Override
    public void cacheSmsCode(long mobile,String code) {
        redisTemplate.opsForValue().set(RedisConstant.SMS_CODE + mobile, code, expire, TimeUnit.SECONDS);
    }

    /**
     * 校验短信验证码
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public boolean checkSmsCode(long mobile, String code) {
        String cacheCode = redisTemplate.opsForValue().get(RedisConstant.SMS_CODE + mobile);
        return !StringUtils.isEmpty(cacheCode) && cacheCode.equals(code);
    }

    /**
     * 获取随机6位数验证码
     * @return
     */
    protected String createCode(){
        int random = (int)((Math.random()*9+1)*100000);
        return String.valueOf(random);
    }


}
