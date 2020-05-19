package cn.poile.blog.common.sms;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

/**
 * 腾讯短信服务
 * @author: yaohw
 * @create: 2020-05-18 10:38
 **/
@Log4j2
public class TencentSmsCodeService extends BaseSmsCodeService{

    private String appId;
    private String appKey;
    private String templateId;
    private String signName;


    public TencentSmsCodeService (SmsServiceProperties properties) {

    }

    /**
     * 发送短信验证码实现
     *
     * @param mobile 手机号
     * @return
     */
    @Override
    protected SendResult handleSendSmsCode(long mobile) {
        SmsSingleSender sender = new SmsSingleSender(Integer.parseInt(appId), appKey);
        ArrayList<String> params = new ArrayList<>();
        String code = createCode();
        params.add(code);
        // 默认只能发送中国大陆的短信86
        try {
            SmsSingleSenderResult result = sender.sendWithParam("86", Long.toString(mobile), Integer.parseInt(templateId), params, signName, "", "");
            if (result.result != 0) {
                throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), result.errMsg);
            }
            return new SendResult(true,code);
        } catch (Exception e) {
            log.error("发送短信失败:{0}", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "短信发送失败");
        }
    }
}
