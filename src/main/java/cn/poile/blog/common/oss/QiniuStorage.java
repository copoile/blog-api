package cn.poile.blog.common.oss;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云 存储
 *
 * @author: yaohw
 * @create: 2019-10-31 19:49
 **/
@Log4j2
public class QiniuStorage extends AbstractStorage {

    private UploadManager uploadManager;

    private BucketManager bucketManager;

    private String token;

    private String domain;

    private String bucket;


    public QiniuStorage(StorageProperties.Qiniu qiniu, Zone zone) {
        Configuration configuration = new Configuration(zone);
        this.uploadManager = new UploadManager(configuration);
        this.bucket = qiniu.getBucket();
        this.token = Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey()).uploadToken(bucket);
        this.bucketManager = new BucketManager(Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey()), configuration);
        this.domain = qiniu.getDomain();
    }


    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param path 文件路径
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(byte[] bytes, String path, String contentType) {
        try {
            Response res = uploadManager.put(bytes, path, token, null, contentType, false);
            if (!res.isOK()) {
                log.error("七牛云上传文件失败:{}", res);
                throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"上传文件失败");
            }
        } catch (QiniuException ex) {
            log.error("七牛云上传文件失败:{0}", ex);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"上传文件失败");
        }
        return domain + path;
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path 文件路径
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return upload(bytes, path, contentType);
        } catch (IOException ex) {
            log.error("七牛云上传文件失败:{0}", ex);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"上传文件失败");
        }
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
        try {
            Response res = bucketManager.delete(bucket, getFileNameFromFullPath(fullPath));
            if (!res.isOK()) {
                log.error("删除文件失败:{}",res);
                throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"删除文件失败");
            }
            return true;
        } catch (QiniuException ex) {
            log.error("删除文件失败:{0}",ex);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"删除文件失败");
        }
    }
}
