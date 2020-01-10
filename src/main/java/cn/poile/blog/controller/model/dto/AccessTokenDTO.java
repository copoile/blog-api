package cn.poile.blog.controller.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="AccessTokenDTO", description="AccessTokenDTO")
public class AccessTokenDTO {
    @ApiModelProperty("access_token")
    @JsonProperty("access_token")
    private String accessToken;

    @ApiModelProperty("token类型:Bearer")
    private String tokenType;

    @ApiModelProperty("refresh_token")
    @JsonProperty("refresh_token")
    private String refreshToken;
}
