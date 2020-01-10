package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.CommonConstant;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.entity.Tag;
import cn.poile.blog.mapper.TagMapper;
import cn.poile.blog.service.ITagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-14
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    /**
     * 新增标签
     *
     * @param tagName
     * @return void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTag(String tagName) {
        Tag daoTag = selectByTagName(tagName);
        if (daoTag != null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setDeleted(CommonConstant.NOT_DELETED);
        save(tag);
    }

    /**
     *  分页查询标签
     * @param current 当前页
     * @param size 每页数量
     * @param tagName 标签名
     * @return com.baomidou.mybatisplus.core.metadata.IPage<cn.poile.blog.entity.Tag>
     */
    @Override
    public IPage<Tag> selectTagPage(long current,long size,String tagName) {
        Page<Tag> page = new Page<>(current,size);
        if (StringUtils.isBlank(tagName)) {
            return page(page);
        }
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(Tag::getName,tagName);
        return page(page,queryWrapper);
    }

    /**
     * 标签列表
     * @param tagName
     * @return java.util.List<cn.poile.blog.entity.Tag>
     */
    @Override
    public List<Tag> selectTagList(String tagName) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tagName)) {
            queryWrapper.lambda().like(Tag::getName,tagName);
        }
        return list(queryWrapper);
    }


    /**
     * 修改标签
     * @param id
     * @param tagName
     */
    @Override
    public void update(int id, String tagName) {
        Tag daoTag = selectByTagName(tagName);
        if (daoTag != null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"标签已存在");
        }
        removeById(id);
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setId(id);
        updateById(tag);
    }

    /**
     * 删除标签
     *
     * @param id
     */
    @Override
    public void delete(int id) {
        removeById(id);
    }

    /**
     * 根据标签名查询
     * @param tagName
     * @return cn.poile.blog.entity.Tag
     */
    private Tag selectByTagName(String tagName){
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Tag::getName,tagName);
        return getOne(queryWrapper,false);
    }
}
