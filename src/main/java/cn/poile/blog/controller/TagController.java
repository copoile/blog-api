package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.entity.Tag;
import cn.poile.blog.service.ITagService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-11-14
 */
@RestController
@RequestMapping("/tag")
@Api(tags = "标签服务", value = "/tag")
public class TagController extends BaseController {

    @Autowired
    private ITagService tagService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "添加标签", notes = "需要accessToken，需要管理员权限")
    public ApiResponse addTag(@NotBlank(message = "标签名不能为空") @RequestParam("tagName") String tagName) {
        tagService.addTag(tagName);
        return createResponse();
    }


    @GetMapping("/page")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "分页查询标签", notes = "需要accessToken，需要管理员权限")
    public ApiResponse<IPage<Tag>> page(@ApiParam("当前页") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                        @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
                                        @ApiParam("标签名关键字，可空") @RequestParam(value = "tagName", required = false) String tagName) {
        return createResponse(tagService.selectTagPage(current, size, tagName));
    }


    @GetMapping("/list")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "获取标签列表", notes = "需要accessToken，需要管理员权限")
    public ApiResponse<List<Tag>> list() {
        return createResponse(tagService.selectTagList());
    }


    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "修改标签名",notes = "需要accessToken，需要管理员权限")
    public ApiResponse update(@ApiParam("标签id") @PathVariable("id") int id,
                              @ApiParam("标签名") @RequestParam(value = "tagName") String tagName) {
        tagService.update(id,tagName);
        return createResponse();
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "删除标签",notes = "需要accessToken,，需要管理员权限，硬删除，文章冗余字段，不会对文章有影响")
    public ApiResponse delete(@ApiParam("标签id") @PathVariable("id") int id) {
        tagService.delete(id);
        return createResponse();
    }

}
