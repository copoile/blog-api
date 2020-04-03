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

import javax.validation.constraints.NotBlank;

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

    @NotBlank(message = "客户端ID不能为空")
    @ApiModelProperty(value = "客户端id，客户端唯一标识")
    private String clientId;

    @NotBlank(message = "客户端秘钥不能为空")
    @ApiModelProperty(value = "客户端秘钥")
    private String clientSecret;

    @ApiModelProperty(value = "access_token有效时长")
    private Long accessTokenExpire;

    @ApiModelProperty(value = "refresh_token有效时长")
    private Long refreshTokenExpire;

    @ApiModelProperty(value = "是否启用refresh_token,1:是，0:否")
    private Integer enableRefreshToken;


}
