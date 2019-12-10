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
    INVALID_REQUEST(1011,"无效请求或请求不接受"),

    /**
     * 接口限流
     */
    REQUEST_LIMIT(1012,"接口访问次数限制"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(1013,"系统异常");

    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误信息
     */
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

