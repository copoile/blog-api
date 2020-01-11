package cn.poile.blog.controller;


import cn.poile.blog.common.oss.Storage;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.entity.FriendChain;
import cn.poile.blog.service.IFriendChainService;
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
public class FriendChainController extends BaseController {

    @Autowired
    private IFriendChainService friendLinkService;

    @Autowired
    private Storage storage;

    @PostMapping("/save")
    @ApiOperation(value = "新增或更新友链", notes = "id不为空时更新，需要accessToken，需要管理员权限")
    public ApiResponse add(@RequestBody FriendChain friendChain) {
        friendLinkService.saveOrUpdate(friendChain);
        return createResponse();
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取友链列表")
    public ApiResponse<List<FriendChain>> list() {
        return createResponse(friendLinkService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取友链列表")
    public ApiResponse<IPage<FriendChain>> page(
            @ApiParam("页码") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size) {
        Page<FriendChain> page = new Page<>(current, size);
        return createResponse(friendLinkService.page(page));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除友链")
    public ApiResponse delete(@ApiParam("友链id") @PathVariable(value = "id") int id) {
        FriendChain friendChain = friendLinkService.getById(id);
        if (friendChain != null && StringUtils.isNotBlank(friendChain.getIcon())) {
            deleteIcon(friendChain.getIcon());
        }
        friendLinkService.removeById(id);
        return createResponse();
    }

    /**
     * 删除icon
     * @param path
     */
    private void deleteIcon(String path) {
        storage.delete(path);
    }

}
