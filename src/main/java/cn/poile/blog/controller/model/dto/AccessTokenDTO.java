package cn.poile.blog.controller.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: yaohw
 * @create: 2019-11-04 17:03
 **/
@Data
@Accessors(chain = true)
@ApiModel(value="AccessTokenDTO", description="AccessTokenDTO")
public class AccessTokenDTO {
    @ApiModelProperty("access_token")
    @JsonProperty("access_token")
    private String accessToken;

    @ApiModelProperty("token类型:Bearer")
    @JsonProperty("token_type")
    private String tokenType;

    @ApiModelProperty("时效")
    private Long expire;

    @ApiModelProperty("refresh_token")
    @JsonProperty("refresh_token")
    private String refreshToken;
}
