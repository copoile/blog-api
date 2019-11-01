package cn.poile.blog.common.oss;

import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.InputStream;

/**
 * 七牛云 存储
 * @author: yaohw
 * @create: 2019-10-31 19:49
 **/
public class QiniuStorage implements Storage{

    private UploadManager uploadManager;

    private String token;

    private String domain;


    public QiniuStorage(StorageProperties.Qiniu qiniu) {
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        token = Auth.create(qiniu.getAccessKey(),qiniu.getSecretKey()).uploadToken(qiniu.getBucket());
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
        return null;
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
