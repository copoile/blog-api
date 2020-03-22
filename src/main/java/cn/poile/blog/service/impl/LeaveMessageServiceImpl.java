package cn.poile.blog.service.impl;

import cn.poile.blog.biz.AsyncService;
import cn.poile.blog.biz.EmailService;
import cn.poile.blog.common.constant.CommonConstant;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.constant.RoleConstant;
import cn.poile.blog.common.constant.UserConstant;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServerSecurityContext;
import cn.poile.blog.entity.LeaveMessage;
import cn.poile.blog.entity.User;
import cn.poile.blog.mapper.LeaveMessageMapper;
import cn.poile.blog.service.ILeaveMessageService;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.CustomUserDetails;
import cn.poile.blog.vo.LeaveMessageVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 留言表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-05
 */
@Service
public class LeaveMessageServiceImpl extends ServiceImpl<LeaveMessageMapper, LeaveMessage> implements ILeaveMessageService {


    @Autowired
    private IUserService userService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private EmailService emailService;

    @Value("${mail.message}")
    private String url;

    /**
     * 新增留言
     *
     * @param content
     */
    @Override
    public void add(String content) {
        LeaveMessage leaveMessage = new LeaveMessage();
        CustomUserDetails userDetail = ServerSecurityContext.getUserDetail(true);
        leaveMessage.setFromUserId(userDetail.getId());
        leaveMessage.setContent(content);
        leaveMessage.setCreateTime(LocalDateTime.now());
        leaveMessage.setDeleted(CommonConstant.NOT_DELETED);
        save(leaveMessage);
        // 异步发送留言提醒邮件给管理员
        asyncSendEmailToAdmin(content, userDetail.getNickname());
    }

    /**
     * 留言回复
     *
     * @param pid
     * @param toUserId
     * @param content
     */
    @Override
    public void reply(Integer pid, Integer toUserId, String content) {
        LeaveMessage leaveMessage = new LeaveMessage();
        leaveMessage.setPid(pid);
        CustomUserDetails userDetail = ServerSecurityContext.getUserDetail(true);
        leaveMessage.setFromUserId(userDetail.getId());
        leaveMessage.setToUserId(toUserId);
        leaveMessage.setContent(content);
        leaveMessage.setCreateTime(LocalDateTime.now());
        leaveMessage.setDeleted(CommonConstant.NOT_DELETED);
        save(leaveMessage);
        // 异步发送留言回复提醒邮件
        asyncSendEmailToTargetAndAdmin(toUserId, content);
    }


    /**
     * 分页获取留言及回复列表
     *
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage<LeaveMessageVo> page(long current, long size) {
        QueryWrapper<LeaveMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(LeaveMessage::getPid);
        int count = count(queryWrapper);
        if (count == 0) {
            return new Page<>(current, size);
        }
        List<LeaveMessageVo> records = this.baseMapper.selectLeaveMessageAndReplyList((current - 1) * size, size);
        Page<LeaveMessageVo> page = new Page<>(current, size, count);
        page.setRecords(records);
        return page;
    }

    /**
     * 最新留言
     *
     * @param limit
     * @return
     */
    @Override
    public List<LeaveMessageVo> selectLatest(long limit) {
        return this.baseMapper.selectLatest(limit);
    }

    /**
     * 删除（本人和管理可删除）
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {
        LeaveMessage message = getById(id);
        if (message != null) {
            CustomUserDetails userDetail = ServerSecurityContext.getUserDetail(true);
            List<String> roleList = userDetail.getRoles();
            // 不是本人，也不是管理员不允许删除
            if (!message.getFromUserId().equals(userDetail.getId()) & !roleList.contains(RoleConstant.ADMIN)) {
                throw new ApiException(ErrorEnum.PERMISSION_DENIED.getErrorCode(), ErrorEnum.PERMISSION_DENIED.getErrorMsg());
            }
            removeById(id);
        }
    }

    /**
     * 异步送邮件给管理员
     *
     * @param content
     */
    private void asyncSendEmailToAdmin(String content, String nickname) {
        asyncService.runAsync((r) -> sendEmailToAdmin(content, nickname));
    }


    /**
     * 异步发送邮箱给指定用户并抄送到管理员
     *
     * @param toUserId
     * @param content
     * @return
     */
    private void asyncSendEmailToTargetAndAdmin(Integer toUserId, String content) {
        asyncService.runAsync(r -> sendEmailToTargetAndAdmin(toUserId, content));
    }

    /**
     * 发送邮箱给指定用户并抄送到管理员
     *
     * @param toUserId
     * @param content
     * @return
     */
    private Boolean sendEmailToTargetAndAdmin(Integer toUserId, String content) {
        User user = userService.getById(toUserId);
        if (user != null && !StringUtils.isBlank(user.getEmail())) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(User::getAdmin, UserConstant.ADMIN);
            List<User> userList = userService.list(queryWrapper);
            // 过滤掉邮箱为空的管理员用户和当前用户（有可能当前用户也是管理员）
            // 剩下的邮箱去重
            List<String> adminEmailList = userList.stream().filter((u) -> !StringUtils.isBlank(u.getEmail()) && !u.getId().equals(toUserId))
                    .map(User::getEmail).distinct().collect(Collectors.toList());
            int size = adminEmailList.size();
            Map<String, Object> params = new HashMap<>(3);
            params.put("url", url);
            params.put("nickname", user.getNickname());
            params.put("content", content);
            String topic = "留言回复提醒";
            emailService.sendHtmlMail(user.getEmail(), topic, "message_reply", params, adminEmailList.toArray(new String[size]));
        }
        return Boolean.TRUE;
    }

    /**
     * 发送邮件给管理员
     */
    private Boolean sendEmailToAdmin(String content, String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getAdmin, UserConstant.ADMIN);
        List<User> userList = userService.list(queryWrapper);
        // 过滤掉邮箱为空的管理员用户 去重
        List<String> adminEmailList = userList.stream().filter((u) -> !StringUtils.isBlank(u.getEmail())).map(User::getEmail).distinct().collect(Collectors.toList());
        if (!adminEmailList.isEmpty()) {
            String to = adminEmailList.get(0);
            adminEmailList.remove(to);
            Map<String, Object> params = new HashMap<>(3);
            params.put("url", url);
            params.put("nickname", nickname);
            params.put("content", content);
            String topic = "留言提醒";
            int size = adminEmailList.size();
            emailService.sendHtmlMail(to, topic, "message", params, adminEmailList.toArray(new String[size]));
        }
        return Boolean.TRUE;
    }
}
