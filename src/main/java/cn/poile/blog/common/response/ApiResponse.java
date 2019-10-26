package cn.poile.blog.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: yaohw
 * @create: 2019-10-25 16:17
 **/
@ApiModel("api响应json对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @ApiModelProperty("响应码")
    private int errorCode;

    @ApiModelProperty("响应信息")
    private String errorMsg;

    @ApiModelProperty("响应数据")
    private T data;
}
