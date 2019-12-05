package cn.poile.blog.mapper;

import cn.poile.blog.entity.ArticleComment;
import cn.poile.blog.vo.ArticleCommentVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文章评论表 Mapper 接口
 * </p>
 *
 * @author yaohw
 * @since 2019-12-03
 */
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {

    /**
     * 查询文章评论及回复列表，包括评论者和回复者信息
     * @param articleId
     * @param offset
     * @param limit
     * @return
     */
    List<ArticleCommentVo> selectCommentAndReplyList(@Param("offset") long offset,@Param("limit") long limit,@Param("articleId") Integer articleId);

    /**
     * 查询最新评论，包括评论者和文章信息
     * @param limit
     * @return
     */
    List<ArticleCommentVo> selectLatestComment(@Param("limit") long limit);

}
