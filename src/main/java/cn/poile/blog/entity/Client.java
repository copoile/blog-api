package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Client对象", description="客户端表")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "客户端id，客户端唯一标识")
    private String clientId;

    @ApiModelProperty(value = "客户端密码")
    private String clientSecret;

    @ApiModelProperty(value = "access_token有效时长")
    private Long accessTokenExpire;

    @ApiModelProperty(value = "refresh_token_expire有效时长")
    private Long refreshTokenExpire;


}
