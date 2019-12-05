package cn.poile.blog.controller;


import cn.poile.blog.common.oss.Storage;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.entity.FriendLink;
import cn.poile.blog.service.IFriendLinkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 友链表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-12-02
 */
@RestController
@RequestMapping("/friend/link")
@Api(tags = "友链服务", value = "/friend/link")
public class FriendLinkController extends BaseController {

    @Autowired
    private IFriendLinkService friendLinkService;

    @Autowired
    private Storage storage;

    @PostMapping("/save")
    @ApiOperation(value = "新增或更新友链", notes = "id不为空时更新，需要accessToken，需要管理员权限")
    public ApiResponse add(@RequestBody FriendLink friendLink) {
        friendLinkService.saveOrUpdate(friendLink);
        return createResponse();
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取友链列表")
    public ApiResponse<List<FriendLink>> list() {
        return createResponse(friendLinkService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取友链列表")
    public ApiResponse<IPage<FriendLink>> page(
            @ApiParam("页码") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size) {
        Page<FriendLink> page = new Page<>(current, size);
        return createResponse(friendLinkService.page(page));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除友链")
    public ApiResponse delete(@ApiParam("友链id") @PathVariable(value = "id") int id) {
        FriendLink friendLink = friendLinkService.getById(id);
        if (friendLink != null) {
            friendLinkService.removeById(id);
            String icon = friendLink.getIcon();
            if (!StringUtils.isBlank(icon)) {
                storage.delete(icon);
            }
        }
        return createResponse();
    }

}
