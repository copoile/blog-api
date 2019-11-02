package cn.poile.blog.common.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *  阿里云 存储
 * @author: yaohw
 * @create: 2019-11-02 11:39
 **/
@Log4j2
public class AliStorage extends AbstractStorage{

    private OSSClient client;

    private String endpoint;

    private String bucket;

    public AliStorage(StorageProperties.Ali ali) {
        this.bucket = ali.getBucket();
        this.endpoint = ali.getEndpoint();
        client = new OSSClient(this.endpoint,ali.getAccessKeyId(),ali.getAccessKeyIdSecret());
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
        return upload(new ByteArrayInputStream(bytes), path,contentType);
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
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        client.putObject(bucket,path,inputStream,metadata);
        return "https://" + bucket + "." + endpoint + "/" + path;
    }

    /**
     * 删除文件
     *
     * @param fullPath 文件完整路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String fullPath) {
       try {
            client.deleteObject(bucket , getFileNmaeFullPath(fullPath));
        } catch (Exception e) {
            log.error("删除文件失败:{}",e);
            return false;
        }
        return true;
    }
}
