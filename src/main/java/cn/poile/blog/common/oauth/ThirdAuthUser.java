package cn.poile.blog.common.oauth;

import lombok.Data;

/**
 * 第三方用户信息
 * @author: yaohw
 * @create: 2020-05-20 11:12
 **/
@Data
public class ThirdAuthUser {

    /**
     * 第三方用户唯一id
     */
    private String uuid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户网址
     */
    private String blog;

    /**
     * 所在公司
     */
    private String company;

    /**
     * 位置
     */
    private String location;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户备注
     */
    private String remark;
}
