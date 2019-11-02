package cn.poile.blog.common.exception;

import lombok.Data;

/**
 * 自定义异常
 * @author: yaohw
 * @create: 2019-10-24 17:16
 **/
@Data
public class ApiException extends RuntimeException{

    private int errorCode;

    private String errorMsg;

    public ApiException(int errorCode,String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
