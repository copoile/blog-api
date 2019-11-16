package cn.poile.blog.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.poile.blog.common.constant.ArticleStatusEnum;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.entity.Article;
import cn.poile.blog.entity.ArticleTag;
import cn.poile.blog.entity.Category;
import cn.poile.blog.entity.Tag;
import cn.poile.blog.mapper.ArticleMapper;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.service.IArticleTagService;
import cn.poile.blog.service.ICategoryService;
import cn.poile.blog.service.ITagService;
import cn.poile.blog.vo.CustomUserDetails;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITagService tagService;

    @Autowired
    private IArticleTagService articleTagService;


    private final static String SEPARATION = ",";

    /**
     * 保存文章
     *
     * @param request
     */
    @Override
    public void save(ArticleRequest request) {
        Article article = new Article();
        // 文章id处理
        article.setId(request.getId() == null ? null : request.getId() == 0 ? null : request.getId());
        // 获取当前用户信息
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail();
        article.setUserId(userDetail.getId());
        // 获取分类信息
        Category category = categoryService.getById(request.getCategoryId());
        if (category == null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "分类不存在");
        }
        article.setCategoryName(category.getName());
        article.setCategoryId(category.getId());
        // 文章标题、摘要、内容、封面
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCover(request.getCover());
        // 文章未发布状态
        article.setStatus(ArticleStatusEnum.NOT_PUBLISH.getStatus());
        // 时间
        article.setPublishTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        // 文章标签，冗余标签名，使用,分割
        List<Integer> tagIds = request.getTagIds();
        article.setTags(getSeparationTagNameStr(tagIds));
        // 保存或更新文章
        saveOrUpdate(article);
        // 文章-标签 关联
        // 先删除原来的
        QueryWrapper<ArticleTag> deleteWrapper = new QueryWrapper<>();
        Integer articleId = article.getId();
        deleteWrapper.lambda().eq(ArticleTag::getArticleId, articleId);
        articleTagService.remove(deleteWrapper);
        // 批量新增
        List<ArticleTag> articleTagList = tagIds.stream().map((tagId) -> new ArticleTag(articleId, tagId)).collect(Collectors.toList());
        articleTagService.saveBatch(articleTagList);
    }

    /**
     * 获取标签名逗号分隔字符串
     *
     * @param tagIds
     * @return
     */
    private String getSeparationTagNameStr(List<Integer> tagIds) {
        Collection<Tag> tags = tagService.listByIds(tagIds);
        StringBuilder sb = null;
        for (Tag tag : tags) {
            if (sb == null) {
                sb = new StringBuilder(tag.getName());
            } else {
                sb.append(SEPARATION).append(tag.getName());
            }
        }
        return sb == null ? null : sb.toString();
    }


}
