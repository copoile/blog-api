package cn.poile.blog.mapper;

import cn.poile.blog.entity.Article;
import cn.poile.blog.vo.ArticleArchivesVo;
import cn.poile.blog.vo.ArticleVo;
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
     * 分页查询文章，不查询文章内容
     * @param status
     * @param title
     * @param categoryId
     * @param tagId
     * @param start
     * @param end
     * @param offset
     * @param limit
     * @return
     */
    List<ArticleVo> selectArticleVoPage(@Param("status") Integer status, @Param("title") String title,
                                        @Param("categoryId") Integer categoryId,
                                        @Param("tagId") Integer tagId,
                                        @Param("start") String start,
                                        @Param("end")String end,
                                        @Param("offset") long offset,
                                        @Param("limit") long limit);


    /**
     * 分页查询计数
     * @param status 状态
     * @param categoryId 分类id
     * @param tagId 标签id
     * @param start 开始
     * @param end
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



}
