package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.dto.PreArtAndNextArtDTO;
import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.vo.ArticleArchivesVo;
import cn.poile.blog.vo.ArticleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        articleService.save(request);
        return createResponse();
    }


    @PostMapping("/publish")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "保存并发布文章", notes = "需要accessToken，需要管理员权限")
    public ApiResponse publish(@Validated @RequestBody ArticleRequest request) {
        articleService.publish(request);
        return createResponse();
    }

    @GetMapping("/published/page")
    @ApiOperation(value = "分页获取已发布文章",notes = "用于前台页面展示")
    public ApiResponse<IPage<ArticleVo>> publishedPage(@ApiParam("当前页") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                                       @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
                                                       @ApiParam("分类id") @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                                       @ApiParam("标签id") @RequestParam(value = "tagId",required = false) Integer tagId,
                                                       @ApiParam("年月") @RequestParam(value = "yearMonth",required = false) String yearMonth,
                                                       @ApiParam("标题关键字，可空") @RequestParam(value = "title", required = false) String title) {
      return createResponse(articleService.selectPublishedArticleVoPage(current, size, categoryId,tagId,yearMonth,title));
    }


    @GetMapping("/page")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "分页获取文章",notes = "用于后台管理，需要accessToken，需要管理员权限")
    public ApiResponse<IPage<ArticleVo>> page(@ApiParam("当前页") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                                       @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
                                                       @ApiParam("文章状态,可空；0:已发布，1:未发布，2:回收站") @RequestParam(value = "status",required = false) Integer status,
                                                       @ApiParam("标题关键字，可空") @RequestParam(value = "title", required = false) String title) {
        return createResponse(articleService.selectArticleVoPage(current, size, status, title));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除文章",notes = "逻辑删除，需要accessToken，需要管理员权限")
    private ApiResponse delete(@ApiParam("文章id") @PathVariable("id") int id) {
        articleService.delete(id);
        return createResponse();
    }

    @DeleteMapping("/discard/{id}")
    @ApiOperation(value = "丢弃文章(回收站)",notes = "需要accessToken，需要管理员权限")
    private ApiResponse discard(@ApiParam("文章id") @PathVariable("id") int id) {
        articleService.discard(id);
        return createResponse();
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取文章详情信息",notes = "需要accessToken,用于后台文章管理，比列表返回的多一个文章内容，文章分类列表")
    public ApiResponse<ArticleVo> detail(@ApiParam("文章id") @PathVariable("id") int id) {
        return createResponse(articleService.selectArticleVoById(id));
    }

    @GetMapping("/detail/add_view_count/{id}")
    @ApiOperation(value = "获取文章详情信息并新增浏览次数",notes = "比列表返回的多一个文章内容，文章分类列表")
    public ApiResponse<ArticleVo> getOneAndaddViewCount(@ApiParam("文章id") @PathVariable("id") int id) {
        return createResponse(articleService.selectOneAndAddViewCount(id));
    }

    @GetMapping("/pre_next/{id}")
    @ApiOperation(value = "获取当前文章的上下篇文章")
    public ApiResponse<PreArtAndNextArtDTO> preAndNextArt(@ApiParam("文章id") @PathVariable("id") int id) {
        return createResponse(articleService.selectPreAndNext(id));
    }

    @GetMapping("/archives/page")
    @ApiOperation(value = "文章归档分页查询",notes = "按年月归档")
    public ApiResponse<IPage<ArticleArchivesVo>> archives(
            @ApiParam("当前页,非必传，默认为:1") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量，,非必传，默认为:12") @RequestParam(value = "size", required = false, defaultValue = "12") long size) {
        return createResponse(articleService.selectArticleArchives(current, size));
    }

}
