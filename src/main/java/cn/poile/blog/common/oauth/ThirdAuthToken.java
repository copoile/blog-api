package cn.poile.blog.common.oauth;

import lombok.Data;

/**
 * 第三次token
 * @author: yaohw
 * @create: 2020-05-20 11:37
 **/
@Data
public class ThirdAuthToken {
    private String accessToken;
    private int expire;
    private String refreshToken;
    private String uid;
    private String openId;
    private String accessCode;
    private String unionId;
}
