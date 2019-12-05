package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.service.ArticleRecommendService;
import cn.poile.blog.service.IArticleCollectService;
import cn.poile.blog.vo.ArticleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.poile.blog.controller.BaseController;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 文章收藏表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-12-04
 */
@RestController
@RequestMapping("/article/collect")
@Api(tags = "文章收藏服务", value = "/article/collect")
public class ArticleCollectController extends BaseController {

    @Autowired
    private ArticleRecommendService articleRecommendService;

    @Autowired
    private IArticleCollectService articleCollectService;

    @PostMapping("/add")
    @ApiOperation(value = "新增收藏",notes = "需要accessToken")
    public ApiResponse add(@ApiParam(value = "文章id") @NotNull(message = "文章id不能为空") @RequestParam(value = "articleId") Integer articleId) {
        articleCollectService.add(articleId);
        articleRecommendService.asyncRefresh(articleId);
        return createResponse();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除收藏",notes = "需要accessToken")
    public ApiResponse delete(@ApiParam("文章id") @NotNull(message = "文章id不能为空") @RequestParam("articleId") Integer articleId) {
        articleCollectService.delete(articleId);
        articleRecommendService.asyncRefresh(articleId);
        return createResponse();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询收藏文章",notes = "需要accessToken")
    public ApiResponse<IPage<ArticleVo>> page(
            @ApiParam("当前页，默认值：1") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量，默认值为：5") @RequestParam(value = "size", required = false, defaultValue = "5") long size) {
        return createResponse(articleCollectService.page(current,size));
    }

    @GetMapping("/collected/{articleId}")
    @ApiOperation(value = "查询文章是否已收藏，1：是，0：否",notes = "需要accessToken")
    public ApiResponse<Integer> collected(@ApiParam("文章id")@PathVariable("articleId") Integer articleId) {
        return createResponse(articleCollectService.collected(articleId));
    }





}
