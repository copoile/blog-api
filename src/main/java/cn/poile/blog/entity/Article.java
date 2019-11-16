package cn.poile.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Article对象", description="文章表")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文章id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "分类名称-冗余字段")
    private String categoryName;

    @ApiModelProperty(value = "文章分类id")
    private Integer categoryId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章摘要")
    private String summary;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "文章封面")
    private String cover;

    @ApiModelProperty(value = "文章标签，使用，分割-冗余字段")
    private String tags;

    @ApiModelProperty(value = "文章状态：0为正常，1为待发布，2为回收站,3为已删除")
    private Integer status;

    @ApiModelProperty(value = "文章浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "评论数-冗余字段")
    private Integer commentCount;

    @ApiModelProperty(value = "点赞数-冗余字段")
    private Integer likeCount;

    @ApiModelProperty(value = "发布时间")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
