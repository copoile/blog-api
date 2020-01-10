package cn.poile.blog.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author: yaohw
 * @create: 2019-10-25 16:17
 **/
@ApiModel("api响应json对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @NonNull
    @ApiModelProperty("响应码")
    private int code;
    @NonNull
    @ApiModelProperty("响应信息")
    private String message;

    @ApiModelProperty("响应数据")
    private T data;
}
