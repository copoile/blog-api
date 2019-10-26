package cn.poile.blog.mapper;

import cn.poile.blog.entity.User;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author yaohw
 * @since 2019-10-23
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户详细信息
     * @param username
     * @return
     */
    UserVo selectUserVoByUsername(@Param("username") String username);

}
