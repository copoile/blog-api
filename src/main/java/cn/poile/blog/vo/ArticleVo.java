package cn.poile.blog.vo;

import cn.poile.blog.entity.Article;
import cn.poile.blog.entity.Category;
import cn.poile.blog.entity.Tag;
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
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="ArticleVo对象", description="文章详细对象")
public class ArticleVo extends Article {


    @ApiModelProperty("标签列表")
    private List<Tag> tagList;

    @ApiModelProperty("分类列表,顺序:root node2 node3")
    private List<Category> categoryList;

    @ApiModelProperty("上一篇")
    private Article previous;

    @ApiModelProperty("下一篇")
    private Article next;

    @ApiModelProperty("推荐分数")
    private  Double recommendScore;

}
