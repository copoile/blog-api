package cn.poile.blog.controller;

import cn.poile.blog.common.oss.Storage;
import cn.poile.blog.common.response.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author: yaohw
 * @create: 2019-10-31 14:13
 **/
@RestController
@RequestMapping("/file")
@Log4j2
@Api(tags = "文件存储服务",value = "file")
public class FileController extends BaseController {

    @Autowired
    private Storage storage;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "上传文件",notes = "需要accessToken，需要管理员权限")
    public ApiResponse<String> upload(@NotNull @RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        String name = System.currentTimeMillis() + "." +extension;
        String fullPath = storage.upload(file.getInputStream(),name,contentType);
        return createResponse(fullPath);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "删除文件",notes = "需要accessToken，需要管理员权限")
    public ApiResponse delete(@ApiParam("文件全路径") @NotNull @RequestParam("fullPath")String fullPath){
        storage.delete(fullPath);
        return createResponse();
    }
}
