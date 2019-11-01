package cn.poile.blog.common.oss;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云 存储
 *
 * @author: yaohw
 * @create: 2019-10-31 19:49
 **/
@Log4j2
public class QiniuStorage implements Storage {

    private UploadManager uploadManager;

    private String token;

    private String domain;


    public QiniuStorage(StorageProperties.Qiniu qiniu, Zone zone) {
        this.uploadManager = new UploadManager(new Configuration(zone));
        this.token = Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey()).uploadToken(qiniu.getBucket());
        this.domain = qiniu.getDomain();
    }


    /**
     * 文件上传
     *
     * @param bytes       文件字节数组
     * @param path        文件路径
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(byte[] bytes, String path, String contentType) {
        try {
            Response res = uploadManager.put(bytes, path, token,null,contentType,false);
            if (!res.isOK()) {
                log.error("七牛云上传文件失败:{}", res);
            }
        } catch (QiniuException e) {
            log.error("七牛云上传文件失败:{}", e);
        }
        return domain + path;
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path        文件路径
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return upload(bytes, path, contentType);
        } catch (IOException e) {
            log.error("七牛云上传文件失败:{}", e);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String path) {
        return false;
    }
}
