package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.service.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import cn.poile.blog.controller.BaseController;

/**
 * <p>
 * 文章表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@RestController
@RequestMapping("/article")
@Api(tags = "文章服务", value = "/article")
public class ArticleController extends BaseController {

    @Autowired
    private IArticleService articleService;

    @ApiOperation(value = "保存文章", notes = "需要accessToken")
    @PostMapping("/save")
    public ApiResponse save(@Validated @RequestBody ArticleRequest request) {
        articleService.save(request);
        return createResponse();
    }

    @ApiOperation(value = "保存并发布文章", notes = "需要accessToken")
    @PostMapping("/publish")
    public ApiResponse publish(@Validated @RequestBody ArticleRequest request) {
        return createResponse();
    }

}
