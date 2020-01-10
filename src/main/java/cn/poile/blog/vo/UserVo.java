package cn.poile.blog.vo;

import cn.poile.blog.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: yaohw
 * @create: 2019-10-24 16:50
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="UserVo对象", description="用户详细信息")
public class UserVo extends User {

    /**
     * 角色列表
     */
    @ApiModelProperty(value = "角色列表")
    protected List<String> roles;


}
