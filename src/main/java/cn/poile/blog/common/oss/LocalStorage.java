package cn.poile.blog.common.oss;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地 存储
 * @author: yaohw
 * @create: 2019-10-31 19:50
 **/
@Log4j2
public class LocalStorage extends AbstractStorage{

    private String path;

    private String proxy;

    public LocalStorage(StorageProperties.Local local) {
        this.path = local.getPath();
        this.proxy = local.getProxy();
        init();
    }

    private void init () {
        File file = new File(path);
        try {
            FileUtils.forceMkdir(file);
        } catch (IOException ex) {
            log.error("创建本地存储文件目录失败:{0}",ex);
        }
    }

    /**
     * 文件上传
     *
     * @param bytes  件字节数组
     * @param name  文件路径
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(byte[] bytes, String name, String contentType) {
        return upload(new ByteArrayInputStream(bytes),name,contentType);
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param name 文件名
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(InputStream inputStream, String name, String contentType) {
        File file = new File(path + name );
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException ex) {
            log.error("本地文件存储失败:{0}",ex);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"上传文件失败");
        }
        return proxy + name;
    }

    /**
     * 删除文件
     *
     * @param fullPath 文件完整路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String fullPath) {
        if (StringUtils.isBlank(fullPath)) {
            return false;
        }
        return FileUtils.deleteQuietly(new File(path + getFileNameFromFullPath(fullPath)));
    }
}
