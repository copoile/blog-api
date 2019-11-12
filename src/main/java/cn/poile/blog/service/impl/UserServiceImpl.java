package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.constant.RoleConstant;
import cn.poile.blog.common.constant.UserConstant;
import cn.poile.blog.common.email.EmailService;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.util.RandomValueStringGenerator;
import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
import cn.poile.blog.entity.User;
import cn.poile.blog.entity.UserRole;
import cn.poile.blog.mapper.UserMapper;
import cn.poile.blog.service.IUserRoleService;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.CustomUserDetails;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-10-23
 */
@Service
@Log4j2
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private IUserRoleService userRoleService;

    @Autowired
    private RedisTokenStore tokenStore;

    @Value("${mail.prefix}")
    private String prefix;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final static String MAIL_CODE_PREFIX ="mail:code:";

    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

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

    /**
     * 用户注册
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserRegisterRequest request) {
        long mobile = request.getMobile();
        String code = request.getCode();
        checkSmsCode(mobile,code);
        String username = request.getUsername();
        User userDao = selectUserByUsernameOrMobile(username, mobile);
        if (userDao != null && username.equals(userDao.getUsername())) {
            throw new ApiException(ErrorEnum.USERNAME_READY_REGISTER.getErrorCode(),ErrorEnum.USERNAME_READY_REGISTER.getErrorMsg());
        }
        if (userDao != null && mobile == userDao.getMobile()) {
            throw new ApiException(ErrorEnum.MOBILE_READY_REGISTER.getErrorCode(),ErrorEnum.MOBILE_READY_REGISTER.getErrorMsg());
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobile(mobile);
        String suffix = String.valueOf(mobile).substring(7);
        user.setNickname("用户" + suffix);
        user.setGender(UserConstant.GENDER_MALE);
        user.setBirthday(LocalDate.now());
        user.setStatus(UserConstant.STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());
        save(user);
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(RoleConstant.ROLE_NORMAL);
        userRoleService.save(userRole);
        smsCodeService.deleteSmsCode(mobile);
    }

    /**
     * 更新用户信息
     *
     * @param request
     */
    @Override
    public void update(UpdateUserRequest request) {
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail();
        if (userDetail == null || request.getUserId() != userDetail.getId()) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"用户id跟当前用户id不匹配或accessToken信息异常");
        }
        User user = new User();
        BeanUtils.copyProperties(request,user);
        updateById(user);
        userDetail.setBirthday(request.getBirthday());
        userDetail.setGender(request.getGender());
        userDetail.setNickname(request.getNickname());
        userDetail.setBrief(request.getBrief());
        tokenStore.updatePrincipal(userDetail);
    }


    /**
     * 发送邮箱验证链接
     *
     * @param email
     * @return void
     */
    @Override
    public void validateEmail(String email) {
        AuthenticationToken authenticationToken = ServeSecurityContext.getAuthenticationToken();
        Map<String,Object> params = new HashMap<>(1);
        String code = generator.generate();
        String accessToken = authenticationToken.getAccessToken();
        String checkUrl = prefix + "?code=" + code;
        params.put("checkUrl",checkUrl);
        String key = MAIL_CODE_PREFIX + code;
        Map<String,String> map = new HashMap<>(2);
        map.put("access_token",accessToken);
        map.put("email",email);
        redisTemplate.opsForHash().putAll(key,map);
        redisTemplate.expire(key,2L,TimeUnit.HOURS);
       // emailService.sendHtmlMail(email,"邮箱验证","email",params);
    }

    /**
     * 绑定邮箱
     *
     * @param code
     * @return void
     */
    @Override
    public void bindEmail(String code) {
        Map<Object, Object> resultMap = redisTemplate.opsForHash().entries(MAIL_CODE_PREFIX + code);
        if (resultMap.isEmpty()) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"code无效或code已过期");
        }
        String accessToken = (String)resultMap.get("access_token");
        AuthenticationToken authenticationToken = tokenStore.readAccessToken(accessToken);
        CustomUserDetails principal = authenticationToken.getPrincipal();
        User user = new User();
        user.setId(principal.getId());
        String email = (String)resultMap.get("email");
        user.setEmail(email);
        updateById(user);
    }

    /**
     * 校验短信验证码
     * @param mobile
     * @param code
     */
    private void checkSmsCode(long mobile, String code) {
        if (!smsCodeService.checkSmsCode(mobile,code)) {
            throw new ApiException(ErrorEnum.BAD_MOBILE_CODE.getErrorCode(),"验证码不正确");
        }
    }

    /**
     * 根据用户名或手机号查询 User
     * @param username
     * @param mobile
     * @return
     */
    private User selectUserByUsernameOrMobile(String username,long mobile){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username).or().eq("mobile",mobile);
        return getOne(queryWrapper);
    }
}
