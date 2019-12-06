package cn.poile.blog.mapper;

import cn.poile.blog.entity.LeaveMessage;
import cn.poile.blog.vo.LeaveMessageVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 留言表 Mapper 接口
 * </p>
 *
 * @author yaohw
 * @since 2019-12-05
 */
public interface LeaveMessageMapper extends BaseMapper<LeaveMessage> {

    /**
     * 查询留言及留言回复列表，包括留言者和回复者信息
     * @param offset
     * @param limit
     * @return
     */
    List<LeaveMessageVo> selectLeaveMessageAndReplyList(@Param("offset") long offset, @Param("limit") long limit);

    /**
     * 最新留言
     * @param limit
     * @return
     */
    List<LeaveMessageVo> selectLatest(@Param("limit") long limit);

}
