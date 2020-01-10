package cn.poile.blog.common.oss;

import com.qiniu.common.Zone;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * 存储工厂
 * @author: yaohw
 * @create: 2019-10-31 11:43
 **/
@Log4j2
public final class StorageFactory {

    /**
     * 本地
     */
    private static final int LOCAL = 1;
    /**
     * 七牛云
     */
    private static final int QINIU = 2;
    /**
     *  网易云
     */
    private static final int NETEASE = 3;
    /**
     *  阿里云
     */
    private static final int ALI = 4;

    private static final String SUFFIX = "/";

    /**
     * 根据配置信息生成不同 Storage 实例
     * @param properties
     * @return
     */
    public static Storage build(StorageProperties properties) {
        int type = properties.getType();
        if (type == LOCAL) {
            return localStorage(properties.getLocal());
        } else if (type == QINIU) {
            return qiniuStorage(properties.getQiniu());
        } else if (type == NETEASE) {
            return neteaseStorage(properties.getNetease());
        } else if (type == ALI) {
            return aliStorage(properties.getAli());
        }
        log.error("无效存储类型: " + type);
        return null;
    }

    /**
     *  网易云存储
     * @param netease 网易云存储配置
     * @return
     */
    private static NeteaseStorage neteaseStorage(StorageProperties.Netease netease) {
        String accessKey = netease.getAccessKey();
        if (StringUtils.isEmpty(accessKey)) {
            log.error("网易云accessKey未配置,请检查配置信息");
            return null;
        }
        String secretKey = netease.getSecretKey();
        if (StringUtils.isEmpty(secretKey)) {
            log.error("网易云secretKey未配置,请检查配置信息");
            return null;
        }
        String endpoint = netease.getEndpoint();
        if (StringUtils.isEmpty(endpoint)) {
            log.error("网易云endpoint未配置,请检查配置信息");
            return null;
        }
        String bucket = netease.getBucket();
        if (StringUtils.isEmpty(bucket)) {
            log.error("网易云bucket未配置,请检查配置信息");
            return null;
        }
        return new NeteaseStorage(netease);
    }

    /**
     * 本地存储
     * @param local 本地存储配置
     * @return
     */
    private static LocalStorage localStorage(StorageProperties.Local local) {
        String path = local.getPath();
        if (StringUtils.isEmpty(path)) {
            log.error("本地存储路径未配置,请检查配置信息");
            return null;
        }
        local.setPath(addSuffix(path));
        String proxy = local.getProxy();
        if(StringUtils.isEmpty(proxy)) {
            log.error("本地存储代理未配置，请检查配置信息");
        }
        local.setProxy(addSuffix(proxy));
        return new LocalStorage(local);
    }

    /**
     *  七牛云存储
     * @param qiniu 七牛云配置
     * @return
     */
    private static QiniuStorage qiniuStorage(StorageProperties.Qiniu qiniu) {
        String accessKey = qiniu.getAccessKey();
        if (StringUtils.isEmpty(accessKey)) {
            log.error("七牛云accessKey未配置，请检查配置信息");
            return null;
        }
        String secretKey = qiniu.getSecretKey();
        if (StringUtils.isEmpty(secretKey)) {
            log.error("七牛云secretKey未配置,请检查配置信息");
            return null;
        }
        String domain = qiniu.getDomain();
        if (StringUtils.isEmpty(domain)) {
            log.error("七牛云domain未配置，请检查配置信息");
            return null;
        }
        qiniu.setDomain(addSuffix(domain));
        String bucket = qiniu.getBucket();
        if (StringUtils.isEmpty(bucket)) {
            log.error("七牛云bucket未配置，请检查配置信息");
            return null;
        }
        Zone zone = qiniuZone(qiniu.getRegion());
        return new QiniuStorage(qiniu,zone);
    }

    /**
     *  阿里云存储
     * @param ali 阿里云配置
     * @return
     */
    private static AliStorage aliStorage(StorageProperties.Ali ali) {
        String accessKeyId = ali.getAccessKeyId();
        if (StringUtils.isEmpty(accessKeyId)) {
            log.error("阿里云accessKeyId未配置，请检查配置信息");
            return null;
        }
        String accessKeyIdSecret = ali.getAccessKeyIdSecret();
        if (StringUtils.isEmpty(accessKeyIdSecret)) {
            log.error("阿里云accessKeyIdSecret未配置，请检查配置信息");
            return null;
        }
        String endpoint = ali.getEndpoint();
        if (StringUtils.isEmpty(endpoint)) {
            log.error("阿里云endpoint未配置，请检查配置信息");
            return null;
        }
        String bucket = ali.getBucket();
        if (StringUtils.isEmpty(bucket)) {
            log.error("阿里云bucket未配置，请检查配置信息");
            return null;
        }
        return new AliStorage(ali);
    }

    /**
     * 七牛云 区域
     * @param zone
     * @return
     */
    private static Zone qiniuZone(String zone) {
        final String HUANAN = "huanan";
        final String HUABEI = "huabei";
        final String HUADONG = "huadong";
        final String BEIMEI = "beimei";
        final String XINJIAPO = "xinjiapo";
        switch (zone) {
            case HUANAN :
                return Zone.huanan();
            case HUABEI :
                return Zone.huabei();
            case HUADONG :
                return Zone.huadong();
            case BEIMEI :
                return Zone.beimei();
            case XINJIAPO :
                return Zone.xinjiapo();
            default:
                return Zone.autoZone();
        }
    }

    /**
     *  添加 / 后缀
     * @param path
     * @return
     */
    private static String addSuffix(String path) {
        if(!path.endsWith(SUFFIX)) {
            return path + SUFFIX;
        }
        return path;
    }
}
