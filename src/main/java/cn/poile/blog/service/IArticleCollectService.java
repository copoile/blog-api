package cn.poile.blog.service;

import cn.poile.blog.entity.ArticleCollect;
import cn.poile.blog.vo.ArticleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文章收藏表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-04
 */
public interface IArticleCollectService extends IService<ArticleCollect> {

    /**
     * 新增收藏
     * @param articleId
     */
    void add(Integer articleId);

    /**
     * 删除收藏
     * @param articleId
     */
    void delete(Integer articleId);

    /**
     * 分页查询用户收藏文章
     * @param current
     * @param size
     * @return
     */
    IPage<ArticleVo> page(long current, long size);

    /**
     * 文章是否收藏
     * @param articleId
     * @return
     */
    Integer collected(Integer articleId);

}
