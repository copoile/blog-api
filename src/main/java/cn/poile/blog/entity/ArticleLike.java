package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章点赞表
 * </p>
 *
 * @author yaohw
 * @since 2019-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleLike对象", description="文章点赞表")
public class ArticleLike implements Serializable {

    private static final long serialVersionUID = 1L;


    @JsonIgnore
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonIgnore
    @ApiModelProperty(value = "文章id")
    private Integer articleId;

    @ApiModelProperty(value = "用户id")
    private Integer userId;


}
