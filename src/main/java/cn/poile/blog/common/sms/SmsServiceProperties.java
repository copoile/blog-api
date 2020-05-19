package cn.poile.blog.common.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: yaohw
 * @create: 2019-11-05 15:40
 **/
@Data
@ConfigurationProperties(prefix = "sms",ignoreInvalidFields = true)
public class SmsServiceProperties  {

    private int type = 1;

    private long expire = 300L;

    private long dayMax = 10L;

    private final SmsServiceProperties.Ali ali = new SmsServiceProperties.Ali();

    private final SmsServiceProperties.Tencent tencent = new SmsServiceProperties.Tencent();

    public SmsServiceProperties() {
    }

    @Data
    public static class Ali {
       private String regionId = "cn-hangzhou";
       private String accessKeyId;
       private String accessKeySecret;
       private String signName;
       private String templateCode;
    }

    @Data
    public static class Tencent {
        private String appId;
        private String appKey;
        private String templateId;
        private String signName;
    }
}
