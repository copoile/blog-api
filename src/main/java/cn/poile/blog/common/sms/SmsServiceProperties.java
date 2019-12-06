package cn.poile.blog.common.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: yaohw
 * @create: 2019-11-05 15:40
 **/
@ConfigurationProperties(prefix = "sms",ignoreInvalidFields = true)
@Data
public class SmsServiceProperties  {

    private int type = 1;

    private long expire = 300L;

    private final SmsServiceProperties.Ali ali = new SmsServiceProperties.Ali();

    @Data
    public static class Ali {
       private String regionId = "cn-hangzhou";
       private String accessKeyId;
       private String accessKeySecret;
       private String signName;
       private String templateCode;
    }
}
