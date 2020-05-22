package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 第三方登录关联表
 * </p>
 *
 * @author yaohw
 * @since 2020-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OauthUser对象", description="第三方登录关联表")
public class OauthUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "第三方平台的用户唯一id")
    private String uuid;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "认证类型，1：qq，2：github，3：微信，4：gitee")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
