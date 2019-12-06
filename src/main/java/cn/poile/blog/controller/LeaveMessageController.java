package cn.poile.blog.controller;

import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.entity.LeaveMessage;
import cn.poile.blog.service.ILeaveMessageService;
import cn.poile.blog.vo.ArticleCommentVo;
import cn.poile.blog.vo.LeaveMessageVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 留言表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-12-05
 */
@RestController
@RequestMapping("/leave/message")
@Api(tags = "留言服务",value = "/leave/message")
public class LeaveMessageController extends BaseController {


    @Autowired
    private ILeaveMessageService messageService;

    @PostMapping("/add")
    @ApiOperation(value = "新增留言",notes = "需要accessToken")
    public ApiResponse add(@ApiParam("留言内容") @NotBlank(message = "内容不能为空") @RequestParam("content") String content) {
        messageService.add(content);
        return createResponse();
    }

    @PostMapping("/reply")
    @ApiOperation(value = "留言回复",notes = "需要accessToken")
    public ApiResponse reply(
            @ApiParam("父id")  @NotNull(message = "pid不能为空") @RequestParam(value = "pid") Integer pid,
            @ApiParam("被回复者id")  @NotNull(message = "被回复者id不能为空") @RequestParam(value = "toUserId") Integer toUserId,
            @ApiParam("回复内容") @NotBlank(message = "内容不能为空") @RequestParam("content") String content) {
        messageService.reply(pid,toUserId,content);
        return createResponse();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取留言及回复列表",notes = "包括留言者和回复者信息")
    public ApiResponse<IPage<LeaveMessageVo>> page(
            @ApiParam("页码") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size) {
        return createResponse(messageService.page(current,size));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除留言或留言回复",notes = "需要accessToken，本人和管理员可删除")
    public ApiResponse delete(@ApiParam("id") @PathVariable("id") Integer id) {
        messageService.delete(id);
        return createResponse();
    }

    @GetMapping("/latest")
    @ApiOperation(value = "最新留言列表",notes = "包括留言者")
    public ApiResponse<List<LeaveMessageVo>> latest(
            @ApiParam("数量限制,默认值:5") @RequestParam(value = "limit", required = false, defaultValue = "5") long limit) {
        return createResponse(messageService.selectLatest(limit));
    }


}
