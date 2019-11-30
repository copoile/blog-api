package cn.poile.blog.mapper;

import cn.poile.blog.entity.Article;
import cn.poile.blog.vo.ArticleArchivesVo;
import cn.poile.blog.vo.ArticleCategoryStatisticsVo;
import cn.poile.blog.vo.ArticleTagStatisticsVo;
import cn.poile.blog.vo.ArticleVo;
import cn.poile.blog.wrapper.ArticlePageQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询
     * @param queryWrapper
     * @return
     */
    List<ArticleVo> selectArticleVoPage(ArticlePageQueryWrapper queryWrapper);


    /**
     * 分页查询计数
     * @param status 状态
     * @param categoryId 分类id
     * @param tagId 标签id
     * @param start 开始日期
     * @param end 结束日期
     * @param title 标题关键字
     * @return
     */
    Integer selectPageCount(@Param("status") Integer status,
                            @Param("categoryId") Integer categoryId,
                            @Param("tagId") Integer tagId,
                            @Param("start") String start,
                            @Param("end")String end,
                            @Param("title") String title);

    /**
     * 根据文章id查询
     * @param id 文章id
     * @param status
     * @return
     */
    ArticleVo selectArticleVoById(@Param("id") int id,@Param("status") Integer status);

    /**
     * 查询上一篇和下一篇
     * @param id
     * @return
     */
    List<Article> selectPreAndNext(@Param("id") int id);


    /**
     * 文章归档计数
     * @return
     */
    Integer selectArticleArchivesCount();

    /**
     * 文章归档
     * @param offset
     * @param limit
     * @return
     */
    List<ArticleArchivesVo> selectArticleArchives(@Param("offset") long offset,@Param("limit") long limit);

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
     * 标签列表查询文章列表
     * @param tagList
     * @param limit
     * @return
     */
    List<ArticleVo> selectByTagList(@Param("tagList") List<Integer> tagList,@Param("limit") long limit );

}
