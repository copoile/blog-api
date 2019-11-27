package cn.poile.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 归档
 * @author: yaohw
 * @create: 2019-11-27 11:11
 **/
@Data
@ApiModel(value = "ArticleArchivesVo对象",description = "文章归档")
public class ArticleArchivesVo {

    @ApiModelProperty(value = "年月,格式yyyy-mm")
    private String yearMonth;

    @ApiModelProperty(value = "数量")
    private long count;
}
