package cn.poile.blog.common.constant;

/**
 * 错误枚举
 * @author: yaohw
 * @create: 2019-10-25 17:15
 **/
public enum ErrorEnum {
    /**
     * 成功
     */
    SUCCESS(0,"成功"),
    /**
     * 用户名或密码错误
     */
    BAD_CREDENTIALS(1002,"用户名或密码错误"),
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1003,"用户不存在"),
    /**
     * 账号禁用
     */
    ACCOUNT_DISABLE(1004,"账号已禁用"),
    /**
     * 账号已过期
     */
    ACCOUNT_EXPIRED(1005,"账号已过期"),
    /**
     * 账号已锁定
     */
    ACCOUNT_LOCKED(1006,"账号已锁定"),
    /**
     * 凭证已过期
     */
    CREDENTIALS_EXPIRED(1007,"凭证已过期"),
    /**
     * 不允许访问
     */
    ACCESS_DENIED(1008,"不允许访问"),
    /**
     * 无权限访问
     */
    PERMISSION_DENIED(1009,"无权限访问"),
    /**
     * 凭证无效或已过期
     */
    CREDENTIALS_INVALID(1010,"凭证无效或已过期"),
    /**
     * 无效请求
     */
    INVALID_REQUEST(1011,"无效请求"),
    /**
     * 接口限流
     */
    REQUEST_LIMIT(1012,"接口访问次数限制"),
    /**
     * 用户名已注册
     */
    USERNAME_READY_REGISTER(1013,"用户名已被使用"),
    /**
     * 手机号已注册
     */
    MOBILE_READY_REGISTER(1014,"手机号已被注册"),
    /**
     * 手机号验证码不正确
     */
    BAD_MOBILE_CODE(1015,"验证码不正确"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(1089,"系统异常"),
    /**
     * /**
     * 邮箱验证不通过
     */
    EMAIL_INVALID_ERROR(1037, "哎呀，无效链接或链接已失效！");

    private Integer errorCode;

    private String errorMsg;

    ErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

