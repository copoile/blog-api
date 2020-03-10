package cn.poile.blog.service;

import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.entity.Article;
import cn.poile.blog.vo.ArticleArchivesVo;
import cn.poile.blog.vo.ArticleCategoryStatisticsVo;
import cn.poile.blog.vo.ArticleTagStatisticsVo;
import cn.poile.blog.vo.ArticleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @return 返回文章id
     */
    Integer saveOrUpdate(ArticleRequest request);

    /**
     * 分页查询文章
     * @param current
     * @param size
     * @param status
     * @param title
     * @param categoryId
     * @param tagId
     * @param yearMonth
     * @return
     */
    IPage<ArticleVo> selectArticleVoPage(long current, long size, Integer status,String title,Integer categoryId,Integer tagId,String yearMonth);

    /**
     * 分页查询已发布文章
     * @param current
     * @param size
     * @param categoryId
     * @param tagId
     * @param yearMonth
     * @param title
     * @param orderBy
     * @return
     */
    IPage<ArticleVo> selectPublishedArticleVoPage(long current,long size,Integer categoryId,Integer tagId,String yearMonth,String title,String orderBy);

    /**
     * id查询文章详细
     * @param id
     * @return
     */
    ArticleVo selectArticleVoById(int id);

    /**
     * 获取文章详情并增长浏览次数
     * @param id
     * @return
     */
    ArticleVo selectOneAndAddViewCount(int id);

    /**
     * 丢弃文章
     * @param id
     */
    void discard(int id);

    /**
     * 删除文章（逻辑删除）
     * @param id
     */
    void delete(int id);

    /**
     * 分页年月归档查询
     * @param current
     * @param size
     * @return
     */
    IPage<ArticleArchivesVo> selectArticleArchives(long current, long size);

    /**
     * 按分类计数文章数
     * @return
     */
    List<ArticleCategoryStatisticsVo> selectCategoryStatistic();

    /**
     * 按标签计数文章数
     * @return
     */
    List<ArticleTagStatisticsVo> selectTagStatistic();

    /**
     * 相关文章查询
     * @param id
     * @param limit
     * @return
     */
    List<ArticleVo> selectInterrelatedById(Integer id,Long limit);

    /**
     * 更新文章状态
     * @param articleId
     * @param status 0或1
     */
    void updateStatus(Integer articleId,Integer status);

    /**
     * 点赞数自增
     * @param articleId
     */
    void likeCountIncrement(int articleId);

    /**
     * 点赞数自减
     * @param articleId
     */
    void likeCountDecrement(int articleId);

    /**
     * 评论数自增
     * @param articleId
     */
    void commentCountIncrement(int articleId);

    /**
     * 评论数自减
     * @param articleId
     */
    void commentCountDecrement(int articleId);

    /**
     * 收藏数自增
     * @param articleId
     */
    void collectCountIncrement(int articleId);

    /**
     * 收藏数自减
     * @param articleId
     */
    void collectCountDecrement(int articleId);

    /**
     * 分页查询用户收藏文章
     * @param offset
     * @param limit
     * @param userId
     * @return
     */
    List<ArticleVo> selectCollectByUserId(@Param("offset") long offset, @Param("limit") long limit, @Param("userId") Integer userId);

    /**
     * 更新分类名称（分类冗余字段）
     * @param categoryId
     * @param newName
     */
    void updateCategoryName(int categoryId,String newName);

}
