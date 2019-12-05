package cn.poile.blog.service;

import cn.poile.blog.entity.ArticleLike;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文章点赞表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-02
 */
public interface IArticleLikeService extends IService<ArticleLike> {

    /**
     * 查询文章是否已点赞
     *
     * @param articleId
     * @return 1：是，0：否
     */
    Integer liked(Integer articleId);

    /**
     * 文章点赞
     * @param articleId
     */
    void like(Integer articleId);

    /**
     * 取消文章点赞
     * @param articleId
     */
    void cancel(Integer articleId);

}
