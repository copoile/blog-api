package cn.poile.blog.controller;

import cn.poile.blog.common.oss.Storage;
import cn.poile.blog.common.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
public class UploadController extends BaseController {

    @Autowired
    private Storage storage;

    @PostMapping("/upload")
    public ApiResponse upload(@NotNull @RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        String name = System.currentTimeMillis() + "." +extension;
        String fullPath = storage.upload(file.getInputStream(),name,contentType);
        return createResponse(fullPath);
    }
}
