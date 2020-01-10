package cn.poile.blog.service;

import cn.poile.blog.entity.Tag;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-14
 */
public interface ITagService extends IService<Tag> {
    /**
     *  新增标签
     * @param tagName
     * @return void
     */
    void addTag(String tagName);

    /**
     *  分页查询标签
     * @param current 当前页
     * @param size 每页数量
     * @param tagName 标签名模糊查询
     * @return com.baomidou.mybatisplus.core.metadata.IPage<cn.poile.blog.entity.Tag>
     */
    IPage<Tag> selectTagPage(long current,long size,String tagName);

    /**
     * 标签列表
     * @param tagName
     * @return java.util.List<cn.poile.blog.entity.Tag>
     */
    List<Tag> selectTagList(String tagName);


    /**
     * 修改标签
     * @param id
     * @param tagName
     */
    void update(int id,String tagName);

    /**
     * 删除标签
     * @param id
     */
    void delete(int id);

}
