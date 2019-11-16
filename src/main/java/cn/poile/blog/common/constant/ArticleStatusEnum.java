package cn.poile.blog.common.constant;

/**
 * @author: yaohw
 * @create: 2019-11-15 16:30
 **/
public enum ArticleStatusEnum {
    /**
     * 正常
     */
    NORMAL(0, "正常"),
    /**
     * 待发布
     */
    NOT_PUBLISH(1, "待发布"),
    /**
     * 回收站
     */
    RECOVERED(2, "回收站"),
    /**
     * 已删除
     */
    DELETED(3, "已删除");

    private Integer status;

    private String message;

    ArticleStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
