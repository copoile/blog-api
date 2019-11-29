package cn.poile.blog.vo;

import cn.poile.blog.entity.Category;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文章分类统计
 * @author: yaohw
 * @create: 2019-11-28 10:45
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="ArticleCategoriesVo对象", description="文章分类计数")
public class ArticleCategoryStatisticsVo extends Category {

    @TableField(value = "article_count")
    @ApiModelProperty("分类文章数量")
    private int articleCount;
}
