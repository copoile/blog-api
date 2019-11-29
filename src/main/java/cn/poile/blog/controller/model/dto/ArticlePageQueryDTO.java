package cn.poile.blog.controller.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yaohw
 * @create: 2019-11-28 19:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePageQueryDTO {

    private Long current;

    private Long size;

    private Integer categoryId;

    private Integer tagId;

    private String yearMonth;

    private String title;

    private String sort;
}
