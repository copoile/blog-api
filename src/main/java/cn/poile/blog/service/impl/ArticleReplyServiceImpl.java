package cn.poile.blog.service.impl;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poile.blog.biz.AsyncService;
import cn.poile.blog.common.constant.CommonConstant;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.constant.RoleConstant;
import cn.poile.blog.biz.EmailService;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServerSecurityContext;
import cn.poile.blog.entity.ArticleReply;
import cn.poile.blog.entity.User;
import cn.poile.blog.mapper.ArticleReplyMapper;
import cn.poile.blog.service.IArticleReplyService;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.CustomUserDetails;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文章回复表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-03
 */
@Service
public class ArticleReplyServiceImpl extends ServiceImpl<ArticleReplyMapper, ArticleReply> implements IArticleReplyService {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private IUserService userService;

    @Autowired
    private EmailService emailService;

    @Value("${mail.article}")
    private String prefix;

    /**
     * 新增文章评论回复
     * @param articleId
     * @param commentId
     * @param toUserId
     * @param content
     */
    @Override
    public void add(Integer articleId,Integer commentId,Integer toUserId,String content) {
        ArticleReply reply = new ArticleReply();
        reply.setArticleId(articleId);
        reply.setCommentId(commentId);
        reply.setToUserId(toUserId);
        reply.setContent(content);
        CustomUserDetails userDetail = ServerSecurityContext.getUserDetail(true);
        reply.setFromUserId(userDetail.getId());
        reply.setReplyTime(LocalDateTime.now());
        reply.setDeleted(CommonConstant.NOT_DELETED);
        save(reply);
    }

    /**
     * 异步发送回复提醒邮箱
     * @param articleId
     * @param toUserId
     * @param content
     * @return
     */
    @Override
    public void asyncSendMail(Integer articleId,Integer toUserId,String content) {
        asyncService.runAsync((s) -> sendMail(articleId,toUserId,content));
    }


    /**
     * 删除回复
     *
     * @param replyId
     */
    @Override
    public void delete(Integer replyId) {
        ArticleReply reply = getById(replyId);
        if (reply != null) {
            Integer fromUserId = reply.getFromUserId();
            CustomUserDetails userDetail = ServerSecurityContext.getUserDetail(true);
            List<String> roleList = userDetail.getRoles();
            // 不是本人，也不是管理员不允许删除
            if (!fromUserId.equals(userDetail.getId()) & !roleList.contains(RoleConstant.ADMIN)) {
                throw new ApiException(ErrorEnum.PERMISSION_DENIED.getErrorCode(),ErrorEnum.PERMISSION_DENIED.getErrorMsg());
            }
            removeById(replyId);
        }
    }

    /**
     * 发送回复提醒邮箱
     * @param articleId
     * @param toUserId
     * @param content
     * @return
     */
    private Boolean sendMail(Integer articleId,Integer toUserId,String content) {
        User user = userService.getById(toUserId);
        if (user != null && !StringUtils.isBlank(user.getEmail())) {
            Map<String,Object> params = new HashMap<>(3);
            prefix = prefix.endsWith("/") ? prefix : prefix + "/";
            params.put("url",prefix + articleId);
            params.put("nickname",user.getNickname());
            params.put("content",content);
            emailService.sendHtmlMail(user.getEmail(),"回复提醒","article_reply",params);
        }
        return Boolean.TRUE;
    }
}
