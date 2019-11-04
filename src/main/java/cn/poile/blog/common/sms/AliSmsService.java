package cn.poile.blog.common.sms;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yaohw
 * @create: 2019/11/4 11:17 下午
 */
@Log4j2
public class AliSmsService extends BaseSmsService{



    /**
     * 发送短信验证码
     *
     * @param mobile
     * @return
     */
    @Override
    public Map<String,Boolean> handleSendSmsCode(long mobile) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "<accessKeyId>", "<accessSecret>");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", String.valueOf(mobile));
        request.putQueryParameter("SignName", "个人悦读分享");
        request.putQueryParameter("TemplateCode", "SMS_176942058");
        String code = createCode();
        request.putQueryParameter("TemplateParam", code);
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String,Boolean> resultMap = new HashMap<>(1);
            resultMap.put(code,Boolean.TRUE);
            return resultMap;
        } catch (ClientException e) {
            log.error("发送短信失败:{0}", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"短信发送失败");
        }
    }
}
