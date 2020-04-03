package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.dto.CategoryNodeDTO;
import cn.poile.blog.controller.model.request.AddCategoryRequest;
import cn.poile.blog.entity.Category;
import cn.poile.blog.service.ICategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 目录分类表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-11-14
 */
@RestController
@RequestMapping("/category")
@Api(tags = "分类服务",value = "/category")
public class CategoryController extends BaseController {

    @Autowired
    private ICategoryService categoryService;


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "新增分类",notes = "需要accessToken，需要管理员权限")
    public ApiResponse add(@Validated @RequestBody AddCategoryRequest request) {
        categoryService.add(request);
        return createResponse();
    }


    @GetMapping("/tree")
    @ApiOperation(value = "获取分类树",notes = "数据结构为树型结构，需要accessToken，需要管理员权限")
    public ApiResponse<List<CategoryNodeDTO>> tree() {
        return createResponse(categoryService.getCategoryNodeTree());
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取分类列表",notes = "不分上下级，返回所有分类(已删除除外)")
    public ApiResponse<List<Category>> list() {
        return createResponse(categoryService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取分类",notes = "需要accessToken，需要管理员权限")
    public ApiResponse<IPage<Category>> page( @ApiParam("页码") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                              @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size) {
        Page<Category> page = new Page<>(current, size);
        return createResponse(categoryService.page(page));
    }


    @PostMapping("/update")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "修改分类名",notes = "需要accessToken，需要管理员权限")
    public ApiResponse update(@ApiParam("分类id") @RequestParam("id") int id,
                              @ApiParam("标签名") @RequestParam(value = "name") String name) {
        categoryService.updateCategoryById(id,name);
        return createResponse();
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "删除分类",notes = "需要accessToken，逻辑删除,需要管理员权限，若存在子类，则不允许删除")
    public ApiResponse delete(@ApiParam("分类id") @PathVariable("id") int id) {
        categoryService.delete(id);
        return createResponse();
    }

}
