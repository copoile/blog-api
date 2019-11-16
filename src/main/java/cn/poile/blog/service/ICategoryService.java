package cn.poile.blog.service;

import cn.poile.blog.controller.model.dto.CategoryNodeDTO;
import cn.poile.blog.controller.model.request.AddCategoryRequest;
import cn.poile.blog.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 目录分类表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-14
 */
public interface ICategoryService extends IService<Category> {

    /**
     * 新增分类
     * @param request
     */
    void add(AddCategoryRequest request);

    /**
     * 分类目录树
     * @return
     */
    List<CategoryNodeDTO> getCategoryNodeTree();

    /**
     * 修改
     * @param id
     * @param name
     */
    void updateCategoryNameById(int id,String name);

    /**
     * 删除分类
     * @param id
     */
    void delete(int id);

}
