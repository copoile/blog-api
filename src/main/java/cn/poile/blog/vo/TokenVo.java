package cn.poile.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: yaohw
 * @create: 2019-10-28 18:00
 **/
@Data
@Accessors(chain = true)
@ApiModel(value="TokenVo对象", description="token详细")
public class TokenVo {

    @ApiModelProperty("accessToken")
    private String accessToken;

    @ApiModelProperty("token类型:Bearer")
    private String tokenType = "Bearer";

    @ApiModelProperty("时效")
    private String expire;

    @ApiModelProperty("refreshToken")
    private String refreshToken;
}
