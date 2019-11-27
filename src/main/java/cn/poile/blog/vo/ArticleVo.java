package cn.poile.blog.vo;

import cn.poile.blog.entity.Article;
import cn.poile.blog.entity.Category;
import cn.poile.blog.entity.Tag;
import cn.poile.blog.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 文章详细对象
 * @author: yaohw
 * @create: 2019-11-25 11:10
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleVo对象", description="文章详细对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleVo extends Article {


    @ApiModelProperty("标签列表")
    private List<Tag> tagList;

    @ApiModelProperty("分类列表,顺序:root node2 node3")
    private List<Category> categoryList;

}
