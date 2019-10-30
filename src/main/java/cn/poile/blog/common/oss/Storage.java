package cn.poile.blog.common.oss;

import java.io.InputStream;

/**
 * 存储
 * @author: yaohw
 * @create: 2019-10-30 17:39
 **/
public interface Storage {

    /**
     * 文件上传
     * @param bytes 文件字节数组
     * @param path 文件路径
     * @return http地址
     */
    public String upload(byte[] bytes,String path);

    /**
     * 文件上传
     * @param inputStream 字节流
     * @param path 文件路径
     * @return http地址
     */
    public String upload(InputStream inputStream,String path);

    /**
     *  删除文件
     * @param path 文件路径
     * @return 是否删除成功
     */
    public boolean delete(String path);
}
