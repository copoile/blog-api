package cn.poile.blog.service.impl;

import cn.poile.blog.biz.EmailService;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.constant.RoleConstant;
import cn.poile.blog.common.constant.UserConstant;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.oss.Storage;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.util.RandomValueStringGenerator;
import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
import cn.poile.blog.entity.User;
import cn.poile.blog.mapper.UserMapper;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.CustomUserDetails;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private SmsCodeService smsCodeService;

    @Autowired
    private RedisTokenStore tokenStore;

    @Autowired
    private Storage storage;

    @Value("${mail.check}")
    private String prefix;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 邮箱绑定code的redis前缀
     */
    private final static String REDIS_MAIL_CODE_PREFIX = "mail:code:";

    /**
     * 更换手机号 手机号验证redis前缀
     */
    private final static String REDIS_MOBILE_VALIDATED_PREFIX = "mobile:validated:";


    /**
     * 根据用户名查询
     *
     * @param username
     * @return cn.poile.blog.entity.User
     */
    @Override
    public UserVo selectUserVoByUsernameOtherwiseMobile(String username, Long mobile) {
        User user = selectUserByUsernameOtherwiseMobile(username, mobile);
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        Integer admin = user.getAdmin();
        List<String> roleList = new ArrayList<>();
        roleList.add(admin.equals(UserConstant.ADMIN) ? RoleConstant.ADMIN : RoleConstant.ORDINARY);
        userVo.setRoles(roleList);
        return userVo;
    }

    /**
     * 根据用户id获取userVo
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "user", key = "#id")
    public UserVo selectUserVoById(Integer id) {
        User user = getById(id);
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        Integer admin = user.getAdmin();
        List<String> roleList = new ArrayList<>();
        roleList.add(admin.equals(UserConstant.ADMIN) ? RoleConstant.ADMIN : RoleConstant.ORDINARY);
        userVo.setRoles(roleList);
        return userVo;
    }

    /**
     * 用户注册
     *
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterRequest request) {
        long mobile = request.getMobile();
        String code = request.getCode();
        checkSmsCode(mobile, code);
        String username = request.getUsername();
        User userDao = selectUserByUsernameOrMobile(username, mobile);
        if (userDao != null && username.equals(userDao.getUsername())) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), ErrorEnum.INVALID_REQUEST.getErrorMsg());
        }
        if (userDao != null && mobile == userDao.getMobile()) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), ErrorEnum.INVALID_REQUEST.getErrorMsg());
        }
        User user = new User();
        user.setUsername(username);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobile(mobile);
        String suffix = String.valueOf(mobile).substring(7);
        user.setNickname("用户" + suffix);
        user.setGender(UserConstant.GENDER_MALE);
        user.setBirthday(LocalDate.now());
        user.setStatus(UserConstant.STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());
        user.setAdmin(UserConstant.ORDINARY);
        save(user);
        smsCodeService.deleteSmsCode(mobile);
    }

    /**
     * 更新用户信息
     *
     * @param request
     */
    @Override
    public void update(UpdateUserRequest request) {
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        if (!request.getUserId().equals(userDetail.getId())) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "用户id跟当前用户id不匹配");
        }
        User user = new User();
        BeanUtils.copyProperties(request, user);
        Integer userId = request.getUserId();
        user.setId(userId);
        updateById(user);

        tokenStore.clearUserCacheById(userId);
    }


    /**
     * 发送邮箱验证链接
     *
     * @param email
     * @return void
     */
    @Override
    public void validateEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getEmail, email);
        int count = count(queryWrapper);
        if (count != 0) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "邮箱已被使用");
        }
        AuthenticationToken authenticationToken = ServeSecurityContext.getAuthenticationToken(true);
        Map<String, Object> params = new HashMap<>(1);
        RandomValueStringGenerator generator = new RandomValueStringGenerator();
        String code = generator.generate();
        String accessToken = authenticationToken.getAccessToken();
        String checkUrl = prefix + "?code=" + code;
        params.put("checkUrl", checkUrl);

        String key = REDIS_MAIL_CODE_PREFIX + code;
        Map<String, String> map = new HashMap<>(2);
        map.put("access_token", accessToken);
        map.put("email", email);
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, 2L, TimeUnit.HOURS);
        emailService.asyncSendHtmlMail(email, "邮箱验证", "email", params);
    }

    /**
     * 绑定邮箱
     *
     * @param code
     * @return void
     */
    @Override
    public void bindEmail(String code) {
        Map<Object, Object> resultMap = redisTemplate.opsForHash().entries(REDIS_MAIL_CODE_PREFIX + code);
        if (resultMap.isEmpty()) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "code无效或code已过期");
        }
        String accessToken = (String) resultMap.get("access_token");
        String email = (String) resultMap.get("email");
        redisTemplate.delete(REDIS_MAIL_CODE_PREFIX + code);
        // 读取认证信息
        AuthenticationToken authToken = tokenStore.readByAccessToken(accessToken);
        if (authToken == null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "code无效或code已过期");
        }
        CustomUserDetails principal = authToken.getPrincipal();
        User user = new User();
        Integer userId = principal.getId();
        user.setId(userId);
        user.setEmail(email);
        // 数据库数据更新
        updateById(user);
        // 清空用户缓存
        tokenStore.clearUserCacheById(userId);
    }


    /**
     * 更新头像
     *
     * @param file
     * @return void
     */
    @Override
    public void updateAvatar(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        String name = System.currentTimeMillis() + "." + extension;
        try {
            // 上传头像
            String fullPath = storage.upload(file.getInputStream(), name, contentType);
            CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
            User user = new User();
            Integer userId = userDetail.getId();
            user.setId(userId);
            user.setAvatar(fullPath);
            updateById(user);
            // 删除原头像文件
            storage.delete(userDetail.getAvatar());

            // 清空用户缓存
            tokenStore.clearUserCacheById(userId);
        } catch (IOException e) {
            log.error("上传文件失败:{}", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), ErrorEnum.SYSTEM_ERROR.getErrorMsg());
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @return void
     */
    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(oldPassword, userDetail.getPassword());
        if (!matches) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "原密码不正确");
        }
        User user = new User();
        Integer userId = userDetail.getId();
        user.setId(userId);
        String encodePassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodePassword);
        updateById(user);
        // 清空用户缓存
        tokenStore.clearUserCacheById(userId);
    }

    /**
     * 重置密码
     *
     * @param mobile
     * @param code
     * @param password
     * @return void
     */
    @Override
    public void resetPassword(long mobile, String code, String password) {
        // 判断用户是否存在
        UserVo userVo = selectUserVoByUsernameOtherwiseMobile(null, mobile);
        if (userVo == null) {
            throw new ApiException(ErrorEnum.USER_NOT_FOUND.getErrorCode(), ErrorEnum.USER_NOT_FOUND.getErrorMsg());
        }
        // 验证码校验
        checkSmsCode(mobile, code);
        CustomUserDetails userDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userVo, userDetails);
        User newUser = new User();
        Integer userId = userDetails.getId();
        newUser.setId(userId);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(password);
        newUser.setPassword(encodePassword);
        updateById(newUser);

        // 清空用户缓存
        tokenStore.clearUserCacheById(userId);
    }

    /**
     * 更换手机号  验证手机号
     *
     * @param mobile
     * @param code
     * @return void
     */
    @Override
    public void validateMobile(long mobile, String code) {
        checkSmsCode(mobile, code);
        // 经过原手机号验证标识
        redisTemplate.opsForValue().set(REDIS_MOBILE_VALIDATED_PREFIX + mobile, Long.toString(mobile), 5L, TimeUnit.MINUTES);
        smsCodeService.deleteSmsCode(mobile);
    }

    /**
     * 更换手机号 重新绑定
     *
     * @param mobile
     * @param code
     * @return void
     */
    @Override
    public void rebindMobile(long mobile, String code) {
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        long cacheKey = userDetail.getMobile();
        // 判断是否经过步骤一
        String validated = redisTemplate.opsForValue().get(REDIS_MOBILE_VALIDATED_PREFIX + cacheKey);
        if (StringUtils.isBlank(validated)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "未经原手机号验证或验证已超时，请验证原手机号通过后再试");
        }
        // 验证码校验
        checkSmsCode(mobile, code);
        // 判断手机号是否已被注册
        User user = selectUserByUsernameOrMobile(null, mobile);
        if (user != null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), ErrorEnum.INVALID_REQUEST.getErrorMsg());
        }
        User newUser = new User();
        Integer userId = userDetail.getId();
        newUser.setId(userId);
        newUser.setMobile(mobile);
        updateById(newUser);
        // 清空用户缓存
        tokenStore.clearUserCacheById(userId);
        smsCodeService.deleteSmsCode(mobile);
    }


    /**
     * 分页查询用户
     *
     * @param current
     * @param size
     * @param username
     * @param nickname
     * @return
     */
    @Override
    public IPage<User> page(long current, long size, String username, String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().like(User::getUsername, username);
        }
        if (StringUtils.isNotBlank(nickname)) {
            queryWrapper.lambda().like(User::getNickname, nickname);
        }
        return page(new Page<>(current, size), queryWrapper);
    }

    /**
     * 修改用户状态
     *
     * @param userId
     * @param status
     */
    @Override
    public void status(Integer userId, Integer status) {
        // 状态，0：正常，1：锁定，2：禁用，3：过期
        int min = 0;
        int max = 3;
        if (status < min || status > max) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效状态值");
        }
        User daoUser = getById(userId);
        if (daoUser == null) {
            throw new ApiException(ErrorEnum.USER_NOT_FOUND.getErrorCode(), "用户不存在");
        }
        // 数据库数据更新
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        updateById(user);

        // 清空用户缓存
        tokenStore.clearUserCacheById(userId);
    }

    /**
     * 校验短信验证码
     *
     * @param mobile
     * @param code
     */
    private void checkSmsCode(long mobile, String code) {
        if (!smsCodeService.checkSmsCode(mobile, code)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "验证码不正确");
        }
    }

    /**
     * 根据用户名或手机号查询 User
     *
     * @param username
     * @param mobile
     * @return
     */
    private User selectUserByUsernameOrMobile(String username, Long mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, username).or().eq(User::getMobile, mobile);
        return getOne(queryWrapper, false);
    }

    /**
     * username 不为空时使用username查询，否则使用mobile查询
     *
     * @param username
     * @param mobile
     * @return
     */
    private User selectUserByUsernameOtherwiseMobile(String username, Long mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(User::getUsername, username);
        } else {
            queryWrapper.lambda().eq(User::getMobile, mobile);
        }
        return getOne(queryWrapper, false);
    }
}
