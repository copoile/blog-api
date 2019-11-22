package cn.poile.blog.vo;

import cn.poile.blog.entity.User;
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
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel(value="UserVo对象", description="用户详细信息")
public class UserVo extends User {

    /**
     * 角色列表
     */
    @ApiModelProperty(value = "角色列表")
    protected List<String> roleList;


}
