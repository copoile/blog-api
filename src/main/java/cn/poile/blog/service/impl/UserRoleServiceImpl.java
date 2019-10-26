package cn.poile.blog.service.impl;

import cn.poile.blog.entity.UserRole;
import cn.poile.blog.mapper.UserRoleMapper;
import cn.poile.blog.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色用户关联表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-10-24
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
