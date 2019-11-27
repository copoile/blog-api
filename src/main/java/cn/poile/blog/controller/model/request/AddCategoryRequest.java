package cn.poile.blog.controller.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: yaohw
 * @create: 2019-11-14 17:15
 **/
@ApiModel(value = "添加分类json",description = "添加分类")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    @ApiModelProperty(value = "名称")
    private String name;

    @NotNull(message = "parentId不能为空")
    @ApiModelProperty(value = "父类id,添加根目录分类时值为0")
    private Integer parentId;
}
