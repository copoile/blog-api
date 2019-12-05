package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.service.IArticleCommentService;
import cn.poile.blog.vo.ArticleCommentVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 文章评论表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-12-03
 */
@RestController
@RequestMapping("/article/comment")
@Api(tags = "文章评论服务", value = "/article/comment")
public class ArticleCommentController extends BaseController {

    @Autowired
    private IArticleCommentService articleCommentService;


    @PostMapping("/add")
    @ApiOperation(value = "新增文章评论",notes = "需要accessToken")
    public ApiResponse add(@ApiParam(value = "文章id") @NotNull(message = "文章id不能为空") @RequestParam(value = "articleId") Integer articleId,
                           @ApiParam(value = "评论内容") @NotBlank(message = "评论内容不能为空") @RequestParam(value = "content") String content) {
        articleCommentService.add(articleId,content);
        articleCommentService.asyncRefreshRecommendAndSendCommentMail(articleId,content);
        return createResponse();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除文章评论",notes = "逻辑删除，需要accessToken,管理员或评论发表者可删除")
    public ApiResponse delete(@ApiParam(value = "评论id") @NotNull(message = "评论id不能为空") @RequestParam(value = "commentId") Integer commentId) {
        articleCommentService.delete(commentId);
        return createResponse();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取文章评论及回复列表",notes = "包括评论者和回复者信息")
    public ApiResponse<IPage<ArticleCommentVo>> page(
            @ApiParam("页码") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
            @ApiParam(value = "文章id") @NotNull(message = "文章id不能为空") @RequestParam(value = "articleId") Integer articleId) {
        return createResponse(articleCommentService.selectCommentAndReplyList(current,size,articleId));
    }

    @GetMapping("/latest")
    @ApiOperation(value = "最新文章评论列表",notes = "包括文章信息和评论者信息")
    public ApiResponse<List<ArticleCommentVo>> latest(
            @ApiParam("数量限制,默认值:5") @RequestParam(value = "limit", required = false, defaultValue = "5") long limit) {
        return createResponse(articleCommentService.selectLatestComment(limit));
    }
}
