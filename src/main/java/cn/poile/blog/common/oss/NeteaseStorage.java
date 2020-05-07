package cn.poile.blog.common.oss;
import com.google.common.collect.Lists;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.util.AbstractListWarp;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.Protocol;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.Upload;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 网易云 存储
 * @author: yaohw
 * @create: 2019-10-31 11:41
 **/
@Log4j2
public class NeteaseStorage extends AbstractStorage{

    private NosClient nosClient;

    private TransferManager transferManager;

    private String bucket;

    private String nosEndpoint;

    public NeteaseStorage(StorageProperties.Netease netease) {
        this.bucket = netease.getBucket();
        this.nosEndpoint = netease.getEndpoint();
        Credentials credentials = new BasicCredentials(netease.getAccessKey(),netease.getSecretKey());
        ClientConfiguration conf = new ClientConfiguration();
        conf.setProtocol(Protocol.HTTPS);
        this.nosClient = new NosClient(credentials,conf);
        this.nosClient.setEndpoint(netease.getEndpoint());
        this.transferManager =  new TransferManager(nosClient);
    }



    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param path  文件路径
     * @param contentType 文件类型
     * @return http地址
     */
    @Override
    public String upload(byte[] bytes, String path,String contentType) {
        return upload(new ByteArrayInputStream(bytes),path,contentType);
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
    public String upload(InputStream inputStream, String path,String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, inputStream, objectMetadata);
        Upload upload = transferManager.upload(putObjectRequest);
        try {
            upload.waitForUploadResult();
            return "https://" + bucket + "." + nosEndpoint + "/" + path;
        } catch (Exception ex) {
            log.error("网易云存储上传文件失败:{0}",ex);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"文件上传失败");
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
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket).withKeys(getFileNameFromFullPath(fullPath));
        try {
            nosClient.deleteObjects(request);
        } catch (Exception e) {
            log.error("删除图片失败:{}", e);
            return false;
        }
        return true;
    }

    /**
     * 分页获取文件对象列表
     * @param nextMarker
     * @param size
     * @return
     */
    @Override
    public PageStorageObject page(String nextMarker, int size) {
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(bucket);
        request.setMarker(nextMarker);
        request.setMaxKeys(size);
        ObjectListing listObjects = nosClient.listObjects(request);
        List<NOSObjectSummary> objectSummaries = listObjects.getObjectSummaries();
        PageStorageObject page = new PageStorageObject();
        page.setRecords(warpList(objectSummaries));
        page.setNextMarker(listObjects.getNextMarker());
        page.setCurrentMarker(nextMarker);
        page.setLoadedAll(!listObjects.isTruncated());
        page.setSize(size);
        return page;
    }

    /**
     * 列表类型转换
     * @param sourceList
     * @return
     */
    private List<StorageObject> warpList(List<NOSObjectSummary> sourceList) {
        return new AbstractListWarp<NOSObjectSummary,StorageObject>() {
            @Override
            public StorageObject warp(NOSObjectSummary source) {
                StorageObject storageObject = new StorageObject();
                String key = source.getKey();
                storageObject.setName(key);
                storageObject.setPath( "/" + key);
                storageObject.setDate(source.getLastModified());
                storageObject.setUrl("https://" + bucket + "." + nosEndpoint + "/" + key);
                return storageObject;
            }
        }.warpList(sourceList);
    }
}
