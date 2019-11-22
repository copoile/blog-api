package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.service.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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




    @PostMapping("/save")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "保存文章", notes = "需要accessToken，需要管理员权限")
    public ApiResponse save(@Validated @RequestBody ArticleRequest request) {
        // articleService.save(request);
        return createResponse();
    }


    @PostMapping("/publish")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "保存并发布文章", notes = "需要accessToken，需要管理员权限")
    public ApiResponse publish(@Validated @RequestBody ArticleRequest request) {
        articleService.publish(request);
        return createResponse();
    }

}
