package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.entity.ArticleLike;
import cn.poile.blog.mapper.ArticleLikeMapper;
import cn.poile.blog.service.IArticleLikeService;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.vo.CustomUserDetails;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 文章点赞表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-02
 */
@Log4j2
@Service
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLike> implements IArticleLikeService {

    @Autowired
    private IArticleService articleService;

    /**
     * 查询文章是否已点赞
     *
     * @param articleId
     * @return 1：是，0：否
     */
    @Override
    public Integer liked(Integer articleId) {
        QueryWrapper<ArticleLike> queryWrapper = new QueryWrapper<>();
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        queryWrapper.lambda().eq(ArticleLike::getArticleId, articleId).eq(ArticleLike::getUserId,userDetail.getId());
        return count(queryWrapper);
    }

    /**
     * 文章点赞
     *
     * @param articleId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Integer articleId) {
        QueryWrapper<ArticleLike> queryWrapper = new QueryWrapper<>();
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        Integer userId = userDetail.getId();
        queryWrapper.lambda().eq(ArticleLike::getArticleId, articleId).eq(ArticleLike::getUserId,userId);
        int count = count(queryWrapper);
        if (count != 0) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"文章已点赞，不可重复点赞");
        }
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        save(like);
        articleService.likeCountIncrement(articleId);

    }

    /**
     * 取消文章点赞
     *
     * @param articleId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer articleId) {
        QueryWrapper<ArticleLike> queryWrapper = new QueryWrapper<>();
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        queryWrapper.lambda().eq(ArticleLike::getArticleId, articleId).eq(ArticleLike::getUserId,userDetail.getId());
        int count = count(queryWrapper);
        if (count == 0) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"文章未点赞");
        }
        remove(queryWrapper);
        articleService.likeCountDecrement(articleId);
    }
}
