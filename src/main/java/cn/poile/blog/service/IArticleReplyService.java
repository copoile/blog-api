package cn.poile.blog.service;

import cn.poile.blog.entity.ArticleReply;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文章回复表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-03
 */
public interface IArticleReplyService extends IService<ArticleReply> {

    /**
     * 新增文章评论回复
     * @param articleId
     * @param commentId
     * @param toUserId
     * @param content
     */
    void add(Integer articleId,Integer commentId,Integer toUserId,String content);

    /**
     * 删除回复
     * @param replyId
     */
    void delete(Integer replyId);

    /**
     * 异步发送回复提醒邮箱
     * @param articleId
     * @param toUserId
     * @param content
     * @return
     */
    void asyncSendMail(Integer articleId,Integer toUserId,String content);

}
