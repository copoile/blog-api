package cn.poile.blog.service.impl;

import cn.poile.blog.entity.User;
import cn.poile.blog.mapper.UserMapper;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-10-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询
     *
     * @param username
     * @return cn.poile.blog.entity.User
     */
    @Override
    public UserVo selectUserVoByUsernameOrMobile(String username,Long mobile) {
       return userMapper.selectUserVoByUsernameOrMobile(username,mobile);
    }
}
