package cn.poile.blog.service.impl;

import cn.poile.blog.entity.Role;
import cn.poile.blog.mapper.RoleMapper;
import cn.poile.blog.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-10-24
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
