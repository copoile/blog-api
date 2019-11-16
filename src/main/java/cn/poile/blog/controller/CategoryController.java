package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.dto.CategoryNodeDTO;
import cn.poile.blog.controller.model.request.AddCategoryRequest;
import cn.poile.blog.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation(value = "新增目录分类",notes = "需要accessToken")
    @PostMapping("/add")
    public ApiResponse add(@Validated @RequestBody AddCategoryRequest request) {
        categoryService.add(request);
        return createResponse();
    }

    @ApiOperation(value = "获取目录树",notes = "需要accessToken")
    @GetMapping("/tree")
    public ApiResponse<List<CategoryNodeDTO>> tree() {
        return createResponse(categoryService.getCategoryNodeTree());
    }

    @ApiOperation(value = "修改分类名",notes = "需要accessToken")
    @PostMapping("/update/{id}")
    public ApiResponse update(@ApiParam("分类id") @PathVariable("id") int id,
                              @ApiParam("标签名") @RequestParam(value = "categoryName") String categoryName) {
        categoryService.updateCategoryNameById(id,categoryName);
        return createResponse();
    }

    @ApiOperation(value = "删除分类",notes = "硬删除，需要accessToken，存在子类不允许删除")
    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@ApiParam("分类id") @PathVariable("id") int id) {
        categoryService.delete(id);
        return createResponse();
    }

}
