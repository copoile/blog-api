package cn.poile.blog.service;

import cn.poile.blog.controller.model.dto.AccessTokenDTO;
import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
import cn.poile.blog.entity.User;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

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
    UserVo selectUserVoByUsernameOtherwiseMobile(String username, Long mobile);

    /**
     * 用户注册
     * @param request
     */
   void register(UserRegisterRequest request);

    /**
     * 更新用户信息
     * @param request
     */
    void update(UpdateUserRequest request);

    /**
     *  发送邮箱验证链接
     * @param email
     * @return void
     */
    void validateEmail(String email);

    /**
     *  绑定邮箱
     * @param code
     * @return void
     */
    AccessTokenDTO bindEmail(String code);

    /**
     *  更新头像
     * @param file
     * @return void
     */
    void updateAvatar(MultipartFile file);

    /**
     *  修改密码
     * @param oldPassword
     * @param newPassword
     * @return void
     */
    void updatePassword(String oldPassword,String newPassword);

    /**
     *  重置密码
     * @param mobile
     * @param code
     * @param password
     * @return void
     */
    void resetPassword(long mobile,String code,String password);

    /**
     * 更换手机号  验证手机号
     * @param mobile
     * @param code
     * @return void
     */
    void validateMobile(long mobile,String code);

    /**
     * 更换手机号 重新绑定
     * @param mobile
     * @param code
     * @return void
     */
    void rebindMobile(long mobile,String code);
}
