package cn.poile.blog.vo;

import cn.poile.blog.entity.Tag;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: yaohw
 * @create: 2019-11-28 15:07
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="ArticleTagStatisticsVo对象", description="文章标签计数")
public class ArticleTagStatisticsVo extends Tag {

    @TableField(value = "article_count")
    @ApiModelProperty("分类文章数量")
    private int articleCount;
}
