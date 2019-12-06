package cn.poile.blog.service.impl;

import cn.poile.blog.biz.AsyncService;
import cn.poile.blog.common.constant.CommonConstant;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.constant.RoleConstant;
import cn.poile.blog.biz.EmailService;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.entity.Article;
import cn.poile.blog.entity.ArticleComment;
import cn.poile.blog.entity.User;
import cn.poile.blog.mapper.ArticleCommentMapper;
import cn.poile.blog.service.ArticleRecommendService;
import cn.poile.blog.service.IArticleCommentService;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.ArticleCommentVo;
import cn.poile.blog.vo.CustomUserDetails;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章评论表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-03
 */
@Service
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements IArticleCommentService {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ArticleRecommendService recommendService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IUserService userService;

    @Autowired
    private AsyncService asyncService;

    @Value("${mail.article}")
    private String prefix;


    /**
     * 新增文章评论
     *
     * @param articleId
     * @param content
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Integer articleId, String content) {
        ArticleComment comment = new ArticleComment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setCommentTime(LocalDateTime.now());
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        comment.setFromUserId(userDetail.getId());
        comment.setDeleted(CommonConstant.NOT_DELETED);
        save(comment);
        articleService.commentCountIncrement(articleId);
    }

    /**
     * 异步刷新推荐列表中的评论数、发送评论提醒邮件
     *
     * @param articleId
     * @param content
     * @return
     */
    @Override
    public void asyncRefreshRecommendAndSendCommentMail(Integer articleId, String content) {
        asyncService.runAsync((s) -> refreshRecommendAndSendCommentMail(articleId, content));
    }

    /**
     * 删除评论(逻辑删除),发表评论者和管理员可删除
     *
     * @param commentId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer commentId) {
        ArticleComment comment = getById(commentId);
        if (comment != null) {
            CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
            List<String> roleList = userDetail.getRoleList();
            // 不是本人，也不是管理员不允许删除
            if (!comment.getFromUserId().equals(userDetail.getId()) & !roleList.contains(RoleConstant.ADMIN)) {
                throw new ApiException(ErrorEnum.PERMISSION_DENIED.getErrorCode(), ErrorEnum.PERMISSION_DENIED.getErrorMsg());
            }
            removeById(commentId);
            Integer articleId = comment.getArticleId();
            articleService.commentCountDecrement(articleId);
            recommendService.asyncRefresh(articleId);
        }
    }

    /**
     * 查询文章评论及回复列表，包括评论者和回复者信息
     *
     * @param articleId
     * @return
     */
    @Override
    public IPage<ArticleCommentVo> selectCommentAndReplyList(long current, long size, Integer articleId) {
        QueryWrapper<ArticleComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleComment::getArticleId, articleId);
        int count = count(queryWrapper);
        if (count == 0) {
            return new Page<>(current, size);
        }
        List<ArticleCommentVo> records = this.baseMapper.selectCommentAndReplyList((current - 1) * size, size, articleId);
        Page<ArticleCommentVo> page = new Page<>(current, size, count);
        page.setRecords(records);
        return page;
    }

    /**
     * 查询最新评论，包括评论者和文章信息
     *
     * @param limit
     * @return
     */
    @Override
    public List<ArticleCommentVo> selectLatestComment(long limit) {
        return this.baseMapper.selectLatestComment(limit);
    }

    /**
     * 刷新推荐列表中的评论数、发送评论提醒邮件
     *
     * @param articleId
     * @param content
     * @return
     */
    private Boolean refreshRecommendAndSendCommentMail(Integer articleId, String content) {
        recommendService.refresh(articleId);
        Article article = articleService.getById(articleId);
        if (article != null) {
            Integer userId = article.getUserId();
            User user = userService.getById(userId);
            if (user != null && !StringUtils.isBlank(user.getEmail())) {
                Map<String, Object> params = new HashMap<>(3);
                prefix = prefix.endsWith("/") ? prefix : prefix + "/";
                params.put("url", prefix + articleId);
                params.put("nickname", user.getNickname());
                params.put("content", content);
                String topic = "评论提醒";
                emailService.sendHtmlMail(user.getEmail(), topic, "article_comment", params);
            }
        }
        return Boolean.TRUE;
    }

}
