package cn.poile.blog.service;

import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
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
     *  根据用户名或手机号查询用户信息
     * @param username
     * @param mobile
     * @return cn.poile.blog.entity.User
     */
    public UserVo selectUserVoByUsernameOrMobile(String username,Long mobile);

    /**
     * 用户注册
     * @param request
     */
    public void register(UserRegisterRequest request);

    /**
     * 更新用户信息
     * @param request
     */
    public void update(UpdateUserRequest request);

    /**
     *  发送邮箱验证链接
     * @param email
     * @return void
     */
    public void validateEmail(String email);

    /**
     *  绑定邮箱
     * @param code
     * @return void
     */
    public void bindEmail(String code);
}
