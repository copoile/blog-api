package cn.poile.blog.service.impl;

import cn.poile.blog.entity.Authority;
import cn.poile.blog.mapper.AuthorityMapper;
import cn.poile.blog.service.IAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-10-24
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements IAuthorityService {

}
