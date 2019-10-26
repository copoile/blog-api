package cn.poile.blog.service;

import cn.poile.blog.entity.User;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-10-23
 */
public interface IUserService extends IService<User> {

    /**
     *  根据用户名查询
     * @param username
     * @return cn.poile.blog.entity.User
     */
    public UserVo selectUserVoByUsername(String username);
}
