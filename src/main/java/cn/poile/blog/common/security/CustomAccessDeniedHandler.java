package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.response.ApiResponse;
import com.alibaba.fastjson.JSON;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义无权限访问
 * @author: yaohw
 * @create: 2019-10-25 19:19
 **/
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ApiResponse response = new ApiResponse();
        response.setErrorCode(ErrorEnum.PERMISSION_DENIED.getErrorCode());
        response.setErrorMsg(ErrorEnum.PERMISSION_DENIED.getErrorMsg());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.getWriter().print(JSON.toJSON(response));
    }
}
