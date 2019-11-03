package cn.poile.blog.service.impl;

import cn.poile.blog.common.util.ValidateUtil;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.CustomUserDetails;
import cn.poile.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author: yaohw
 * @create: 2019-10-24 16:40
 **/
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean isMobile = ValidateUtil.validateMobile(username);
        UserVo userVo;
        if (isMobile) {
            userVo = userService.selectUserVoByUsernameOrMobile(null,Long.parseLong(username));
        } else {
            userVo = userService.selectUserVoByUsernameOrMobile(username,null);
        }
        if (userVo == null) {
            throw new UsernameNotFoundException("user not found:" + username);
        }
        UserDetails userDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userVo,userDetails);
        return userDetails;
    }
}
