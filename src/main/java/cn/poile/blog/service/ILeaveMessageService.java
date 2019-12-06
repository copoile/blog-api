package cn.poile.blog.service;

import cn.poile.blog.entity.LeaveMessage;
import cn.poile.blog.vo.LeaveMessageVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 留言表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-05
 */
public interface ILeaveMessageService extends IService<LeaveMessage> {

    /**
     * 新增留言
     *
     * @param content
     */
    void add(String content);

    /**
     * 留言回复
     *
     * @param pid
     * @param toUserId
     * @param content
     */
    void reply(Integer pid, Integer toUserId, String content);

    /**
     * 分页获取留言及回复列表
     * @param current
     * @param size
     * @return
     */
    IPage<LeaveMessageVo> page(long current,long size);

    /**
     * 删除（本人和管理可删除）
     * @param id
     */
    void delete(Integer id);

    /**
     * 最新留言
     * @param limit
     * @return
     */
    List<LeaveMessageVo> selectLatest(long limit);

}
