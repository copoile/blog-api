package cn.poile.blog.common.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 短信发送结果
 * @author: yaohw
 * @create: 2019-12-03 16:43
 **/
@Data
@AllArgsConstructor
public class SendResult {

    /**
     * 是否发送成功
     */
    private boolean success;

    /**
     * 发送的验证码
     */
    private String code;
}
