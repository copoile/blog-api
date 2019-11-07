package cn.poile.blog.common.sms;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信验证码
 * @author: yaohw
 * @create: 2019/11/4 11:17 下午
 */
@Log4j2
public class AliSmsCodeService extends BaseSmsCodeService {

    private String regionId;

    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    private String templateCode;

    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    private static final String ACTION = "SendSms";

    private static final String VERSION = "2017-05-25";


    public AliSmsCodeService(SmsServiceProperties properties) {
        setExpire(properties.getExpire());
        SmsServiceProperties.Ali ali = properties.getAli();
        init(ali);
    }

    private void init(SmsServiceProperties.Ali ali) {
        this.regionId = ali.getRegionId();
        this.accessKeyId = ali.getAccessKeyId();
        this.accessKeySecret = ali.getAccessKeySecret();
        this.signName = ali.getSignName();
        this.templateCode = ali.getTemplateCode();
    }


    /**
     * 发送短信验证码
     *
     * @param mobile
     * @return 返回要求为一个key为验证码，value为短信是否发送成功的一个map
     */
    @Override
    protected Map<String, Boolean> handleSendSmsCode(long mobile) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(DOMAIN);
        request.setVersion(VERSION);
        request.setAction(ACTION);
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", String.valueOf(mobile));
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        String code = createCode();
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            return handleCommonResponse(client.getCommonResponse(request), code);
        } catch (ClientException e) {
            log.error("发送短信失败:{0}", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "短信发送失败");
        }

    }

    /**
     * 短信发送结果解析
     *
     * @param response
     * @param code
     * @return
     */
    private Map<String, Boolean> handleCommonResponse(CommonResponse response, String code) {
        int httpStatus = response.getHttpStatus();
        if (httpStatus != HttpStatus.OK.value()) {
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "短信发送失败");
        }
        String data = response.getData();
        JSONObject jsonObject = JSON.parseObject(data);
        String resultCode = (String) jsonObject.get("Code");
        String successCode = "OK";
        if (!successCode.equals(resultCode)) {
            String resultMessage = (String) jsonObject.get("WebSocketMessageDTO");
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), resultMessage);
        }
        Map<String, Boolean> resultMap = new HashMap<>(1);
        resultMap.put(code, Boolean.TRUE);
        return resultMap;
    }
}
