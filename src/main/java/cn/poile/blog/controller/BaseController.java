package cn.poile.blog.controller;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.response.ApiResponse;

/**
 * @author: yaohw
 * @create: 2019-10-23 12:36
 **/
public class BaseController {

    private <T> ApiResponse<T> init() {
        return new ApiResponse<>();
    }

    protected <T> ApiResponse<T> createResponse() {
        ApiResponse<T> response = init();
        response.setErrorCode(ErrorEnum.SUCCESS.getErrorCode());
        response.setErrorMsg(ErrorEnum.SUCCESS.getErrorMsg());
        return response;
    }

    protected <T> ApiResponse<T> createResponse(T body) {
        ApiResponse<T> response = createResponse();
        response.setBody(body);
        return response;
    }
}
