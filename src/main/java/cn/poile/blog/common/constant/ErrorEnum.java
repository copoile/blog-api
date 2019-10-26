package cn.poile.blog.common.constant;

/**
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
     * 账号禁用
     */
    ACCOUNT_DISABLE(1003,"账号已禁用"),
    /**
     * 账号已过期
     */
    ACCOUNT_EXPIRED(1004,"账号已过期"),
    /**
     * 账号已锁定
     */
    ACCOUNT_LOCKED(1005,"账号已锁定"),
    /**
     * 凭证已过期
     */
    CREDENTIALS_EXPIRED(1006,"凭证已过期"),

    /**
     * 不允许访问
     */
    ACCESS_DENIED(1007,"不允许访问"),




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

