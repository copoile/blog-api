package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.entity.ArticleCollect;
import cn.poile.blog.mapper.ArticleCollectMapper;
import cn.poile.blog.service.IArticleCollectService;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.vo.ArticleArchivesVo;
import cn.poile.blog.vo.ArticleVo;
import cn.poile.blog.vo.CustomUserDetails;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 文章收藏表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-04
 */
@Service
public class ArticleCollectServiceImpl extends ServiceImpl<ArticleCollectMapper, ArticleCollect> implements IArticleCollectService {

    @Autowired
    private IArticleService articleService;

    /**
     * 新增收藏
     *
     * @param articleId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Integer articleId) {
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        QueryWrapper<ArticleCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleCollect::getUserId,userDetail.getId()).eq(ArticleCollect::getArticleId,articleId);
        int count = count(queryWrapper);
        if (count != 0) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"文章已收藏，不可重复收藏");
        }
        ArticleCollect collect = new ArticleCollect();
        collect.setArticleId(articleId);
        collect.setUserId(userDetail.getId());
        save(collect);
        // 文章冗余字段，收藏数自增
        articleService.collectCountIncrement(articleId);
    }

    /**
     * 删除收藏
     *
     * @param articleId
     */
    @Override
    public void delete(Integer articleId) {
        QueryWrapper<ArticleCollect> queryWrapper = new QueryWrapper<>();
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        queryWrapper.lambda().eq(ArticleCollect::getUserId,userDetail.getId()).eq(ArticleCollect::getArticleId,articleId);
        int count = count(queryWrapper);
        if (count == 0) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"文章未收藏");
        }
        remove(queryWrapper);
        // 文章冗余字段，收藏数自减
        articleService.collectCountDecrement(articleId);
    }

    /**
     * 分页查询用户收藏文章
     *
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage<ArticleVo> page(long current, long size) {
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        Integer userId = userDetail.getId();
        QueryWrapper<ArticleCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleCollect::getUserId,userId);
        int count = count(queryWrapper);
        if (count == 0) {
            return new Page<>(current, size);
        }
        List<ArticleVo> articleVoList = articleService.selectCollectByUserId((count - 1) * size,size,userId);
        Page<ArticleVo> page = new Page<>(current, size, count);
        page.setRecords(articleVoList);
        return page;
    }

    /**
     * 文章是否收藏
     *
     * @param articleId
     * @return
     */
    @Override
    public Integer collected(Integer articleId) {
        QueryWrapper<ArticleCollect> queryWrapper = new QueryWrapper<>();
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        queryWrapper.lambda().eq(ArticleCollect::getUserId,userDetail.getId()).eq(ArticleCollect::getArticleId,articleId);
        return count(queryWrapper);
    }
}
