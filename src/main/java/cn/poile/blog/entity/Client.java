package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客户端表
 * </p>
 *
 * @author yaohw
 * @since 2019-12-06
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="Client对象", description="客户端表")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "客户端id，客户端唯一标识")
    private String clientId;

    @ApiModelProperty(value = "客户端密码")
    @JsonProperty("client_secret")
    private String clientSecret;

    @ApiModelProperty(value = "access_token有效时长")
    @JsonProperty("access_token_expire")
    private Long accessTokenExpire;

    @ApiModelProperty(value = "refresh_token_expire有效时长")
    @JsonProperty("refresh_token_expire有效时长")
    private Long refreshTokenExpire;


}
