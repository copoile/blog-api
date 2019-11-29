package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.dto.CategoryNodeDTO;
import cn.poile.blog.controller.model.request.AddCategoryRequest;
import cn.poile.blog.entity.Category;
import cn.poile.blog.service.ICategoryService;
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
@Api(tags = "目录分类服务",value = "/category")
public class CategoryController extends BaseController {

    @Autowired
    private ICategoryService categoryService;


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "新增目录分类",notes = "需要accessToken，需要管理员权限")
    public ApiResponse add(@Validated @RequestBody AddCategoryRequest request) {
        categoryService.add(request);
        return createResponse();
    }


    @GetMapping("/tree")
    @ApiOperation(value = "获取目录树",notes = "数据结构为树型结构")
    public ApiResponse<List<CategoryNodeDTO>> tree() {
        return createResponse(categoryService.getCategoryNodeTree());
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取目录分类列表",notes = "不分上下级，返回所有分类(已删除除外)")
    public ApiResponse<List<Category>> list() {
        return createResponse(categoryService.list());
    }


    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "修改分类名",notes = "需要accessToken，需要管理员权限")
    public ApiResponse update(@ApiParam("分类id") @PathVariable("id") int id,
                              @ApiParam("标签名") @RequestParam(value = "categoryName") String categoryName) {
        categoryService.updateCategoryById(id,categoryName);
        return createResponse();
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "删除分类",notes = "硬删除，需要accessToken，逻辑删除,需要管理员权限，若存在子类，则不允许删除")
    public ApiResponse delete(@ApiParam("分类id") @PathVariable("id") int id) {
        categoryService.delete(id);
        return createResponse();
    }

}
