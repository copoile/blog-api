package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.service.ArticleRecommendService;
import cn.poile.blog.service.IArticleLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 文章点赞表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-12-02
 */
@RestController
@RequestMapping("/article/like")
@Api(tags = "文章点赞服务", value = "/article/like")
public class ArticleLikeController extends BaseController {

    @Autowired
    private IArticleLikeService articleLikeService;

    @Autowired
    private ArticleRecommendService articleRecommendService;


    @GetMapping("/liked/{articleId}")
    @ApiOperation(value = "查询文章是否已点赞",notes = "1：是，0：否")
    public ApiResponse<Integer> liked(@ApiParam("文章id") @PathVariable("articleId") Integer articleId) {
        return createResponse(articleLikeService.liked(articleId));
    }

    @PostMapping("/add")
    @ApiOperation(value = "文章点赞")
    public ApiResponse like(@ApiParam("文章id") @RequestParam("articleId") Integer articleId) {
        articleLikeService.like(articleId);
        articleRecommendService.asyncRefresh(articleId);
        return createResponse();
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "取消文章点赞")
    public ApiResponse cancel(@ApiParam("文章id") @NotNull(message = "文章id不能为空") @RequestParam("articleId") Integer articleId) {
        articleLikeService.cancel(articleId);
        articleRecommendService.asyncRefresh(articleId);
        return createResponse();
    }

}
