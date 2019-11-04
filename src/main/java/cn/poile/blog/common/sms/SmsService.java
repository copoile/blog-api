package cn.poile.blog.common.sms;

/**
 * 短信服务
 * @author: yaohw
 * @create: 2019/11/4 10:34 下午
 */
public interface SmsService {
    /**
     * 发送短信验证码
     * @param mobile
     * @return
     */
    boolean sendSmsCode(long mobile);

    /**
     * 缓存短信验证码
     * @param mobile
     * @param code
     */
    void cacheSmsCode(long mobile,String code);

    /**
     * 校验短信验证码
     * @param mobile
     * @param code
     * @return
     */
    boolean checkSmsCode(long mobile,String code);
}
