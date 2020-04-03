package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.util.AbstractListWarp;
import cn.poile.blog.controller.model.dto.CategoryNodeDTO;
import cn.poile.blog.controller.model.request.AddCategoryRequest;
import cn.poile.blog.entity.Category;
import cn.poile.blog.mapper.CategoryMapper;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.service.ICategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 目录分类表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-14
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private IArticleService articleService;

    /**
     * 根目录父id
     */
    private final static Integer ROOT_PARENT_ID = 0;

    /**
     * 新增分类
     *
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddCategoryRequest request) {
        Category daoCategory = selectByBName(request.getName());
        if (daoCategory != null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "分类已存在");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        save(category);
    }



    /**
     * 分类目录树
     * @return
     */
    @Override
    public List<CategoryNodeDTO> getCategoryNodeTree() {
        List<CategoryNodeDTO> resultList = new ArrayList<>();
        Map<Integer, List<Category>> integerListMap = buildParentId2ChildrenMap(list());
        List<CategoryNodeDTO> rootCategoryNodeDTOList = listWarp(integerListMap.get(ROOT_PARENT_ID));
        rootCategoryNodeDTOList.forEach(
                root -> {
                    resultList.add(recursiveTree(root, integerListMap));
                }
        );
        return resultList;
    }

    /**
     * 修改
     *
     * @param id
     * @param name
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryById(int id, String name) {
        Category daoCategory = selectByBName(name);
        if (daoCategory != null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"分类已存在");
        }
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        updateById(category);
        articleService.updateCategoryName(id,name);
    }

    /**
     * 删除分类
     *
     * @param id
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(int id) {
        Category daoCategory = getById(id);
        if (daoCategory == null) {
            return;
        }
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Category::getParentId,id);
        Category childrenCategory = getOne(queryWrapper,false);
        if (childrenCategory != null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"该分类存在子类,不允许删除");
        }
        removeById(id);
    }


    /**
     * 获取子元素对应父元素列表，顺序为 root node2 node3
     * @param categoryId 当前类别id
     * @return
     */
    @Override
    public List<Category> parentList(Integer categoryId) {
        List<Category> daoCategoryList = list();
        Category currentCategory = null;
        Map<Integer,Category> map = new HashMap<>(daoCategoryList.size());
        for (Category category:daoCategoryList) {
            if (categoryId.equals(category.getId())) {
                currentCategory = category;
            }
            map.put(category.getId(),category);
        }
        List<Category> list = new ArrayList<>();
        List<Category> categoryList = addParent(currentCategory, list,map);
        Collections.reverse(categoryList);
        return categoryList;

    }

    /**
     * 根据子元素递归获取父元素列表
     *
     * @param category
     * @return
     */
    private List<Category> addParent(Category category, List<Category> parentList,Map<Integer,Category> map) {
        if(category == null){
            return parentList;
        }
        parentList.add(category);
        Category parent = map.get(category.getParentId());
        return addParent(parent,parentList,map);
    }

    /**
     * 根据名称查询
     *
     * @param name
     * @return
     */
    private Category selectByBName(String name) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Category::getName,name);
        return getOne(queryWrapper,false);
    }

    /**
     * 递归获取元素
     *
     * @param node
     * @param integerListMap
     * @return
     */
    private CategoryNodeDTO recursiveTree(CategoryNodeDTO node, Map<Integer, List<Category>> integerListMap) {
        node.setChildren(new ArrayList<>());
        List<CategoryNodeDTO> children = listWarp(integerListMap.get(node.getId()));
        if (CollectionUtils.isEmpty(children)) {
            node.setChildren(null);
            return node;
        }
        for (CategoryNodeDTO child : children) {
            CategoryNodeDTO nextChild = recursiveTree(child, integerListMap);
            node.getChildren().add(nextChild);
        }
        return node;
    }

    /**
     * 构建key为ParentId，value为List<Category> children的map
     *
     * @param categoryList
     * @return
     */
    private Map<Integer, List<Category>> buildParentId2ChildrenMap(List<Category> categoryList) {
        Map<Integer, List<Category>> map = new HashMap<>(16);
        for (Category category : categoryList) {
            List<Category> children = map.get(category.getParentId());
            if (children == null) {
                children = new ArrayList<>();
                children.add(category);
                map.put(category.getParentId(), children);
            } else {
                children.add(category);
                map.put(category.getParentId(), children);
            }
        }
        return map;
    }

    /**
     * List<Category> 转 List<CategoryNodeDTO>
     *
     * @param sourceList
     * @return
     */
    private List<CategoryNodeDTO> listWarp(List<Category> sourceList) {
        return new AbstractListWarp<Category, CategoryNodeDTO>() {
            @Override
            public CategoryNodeDTO warp(Category source) {
                CategoryNodeDTO target = new CategoryNodeDTO();
                BeanUtils.copyProperties(source, target);
                return target;
            }
        }.warpList(sourceList);
    }
}
