package cn.poile.blog.service;

import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
public interface IArticleService extends IService<Article> {

    /**
     * 保存文章
     *
     * @param request
     */
    void save(ArticleRequest request);

    /**
     * 发表文章
     * @param request
     */
    void publish(ArticleRequest request);

}
