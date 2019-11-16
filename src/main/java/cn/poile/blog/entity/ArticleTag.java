package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 文章-标签 关联表
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleTag对象", description="文章-标签 关联表")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ArticleTag implements Serializable {


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NonNull
    @ApiModelProperty(value = "文章id")
    private Integer articleId;

    @NonNull
    @ApiModelProperty(value = "标签id")
    private Integer tagId;


}
