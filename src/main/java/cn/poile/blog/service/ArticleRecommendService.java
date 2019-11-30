package cn.poile.blog.service;

import cn.poile.blog.vo.ArticleVo;

import java.util.List;

/**
 * 文章推荐服务接口
 * @author: yaohw
 * @create: 2019-11-29 11:57
 **/
public interface ArticleRecommendService {

    /**
     * 新增推荐
     * @param articleId
     * @param score 分数
     */
    void add(Integer articleId,Double score);

    /**
     * 获取推荐列表
     * @return
     */
    List<ArticleVo> list();

    /**
     * 从推荐中移除
     * @param articleId
     */
    void remove(Integer articleId);

    /**
     * 异步刷新
     * @param articleId
     */
    void asyncRefresh(Integer articleId);

    /**
     * 刷新
     * @param articleId
     */
    void refresh(Integer articleId);
}
