package cn.poile.blog.controller.model.dto;

import cn.poile.blog.entity.Article;
import lombok.Data;

/**
 * 上一篇和下一篇文章DTO
 * @author: yaohw
 * @create: 2019-11-26 19:21
 **/
@Data
public class PreArtAndNextArtDTO {

    /**
     * 上一篇
     */
    private Article pre;

    /**
     * 下一篇
     */
    private Article next;
}
