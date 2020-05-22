package cn.poile.blog.service.impl;

import cn.poile.blog.entity.OauthUser;
import cn.poile.blog.mapper.OauthUserMapper;
import cn.poile.blog.service.IOauthUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 第三方登录关联表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2020-05-20
 */
@Service
public class OauthUserServiceImpl extends ServiceImpl<OauthUserMapper, OauthUser> implements IOauthUserService {

}
