package cn.poile.blog.common.oss;

import com.qiniu.common.Zone;
import lombok.extern.log4j.Log4j2;

/**
 * @author: yaohw
 * @create: 2019-10-31 11:43
 **/
@Log4j2
public final class StorageFactory {

    /**
     * 本地
     */
    private static final int LOCAL = 0;
    /**
     * 七牛云
     */
    private static final int QINIU = 1;
    /**
     *  网易云
     */
    private static final int NETEASE = 2;

    public static Storage build(StorageProperties properties) {
        int type = properties.getType();
        if (type == LOCAL) {
            return localStorage(properties.getLocal());
        } else if (type == QINIU) {
            return qiniuStorage(properties.getQiniu());
        } else if (type == NETEASE) {
            return neteaseStorage(properties.getNetease());
        }
        return new NeteaseStorage(properties.getNetease());
    }

    /**
     *  网易云存储
     * @param netease 网易云存储配置
     * @return
     */
    private static NeteaseStorage neteaseStorage(StorageProperties.Netease netease) {
        String accessKey = netease.getAccessKey();
        if (accessKey.isEmpty()) {
            log.error("网易云accessKey未配置,请检查配置信息");
            return null;
        }
        String secretKey = netease.getSecretKey();
        if (secretKey.isEmpty()) {
            log.error("网易云secretKey未配置,请检查配置信息");
            return null;
        }
        String endpoint = netease.getEndpoint();
        if (endpoint.isEmpty()) {
            log.error("网易云endpoint未配置,请检查配置信息");
            return null;
        }
        String bucket = netease.getBucket();
        if (bucket.isEmpty()) {
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
        log.info("本地存储" + path);
        if (path.isEmpty()) {
            log.error("本地存储路径未配置,请检查配置信息");
            return null;
        }
        String proxy = local.getProxy();
        if(proxy.isEmpty()) {
            log.error("本地存储代理未配置，请检查配置信息");
        }
        return new LocalStorage(local);
    }

    private static QiniuStorage qiniuStorage(StorageProperties.Qiniu qiniu) {
        String accessKey = qiniu.getAccessKey();
        if (accessKey.isEmpty()) {
            log.error("七牛云accessKey未配置，请检查配置信息");
            return null;
        }
        String secretKey = qiniu.getSecretKey();
        if (secretKey.isEmpty()) {
            log.error("七牛云secretKey未配置,请检查配置信息");
            return null;
        }
        String domain = qiniu.getDomain();
        if (domain.isEmpty()) {
            log.error("七牛云domain未配置，请检查配置信息");
            return null;
        }
        String bucket = qiniu.getBucket();
        if (bucket.isEmpty()) {
            log.error("七牛云bucket未配置，请检查配置信息");
            return null;
        }
        Zone zone = qiniuZone(qiniu.getRegion());
        return new QiniuStorage(qiniu,zone);
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
        if (zone.isEmpty()) {
            return Zone.autoZone();
        } else if (HUANAN.equals(zone)) {
            return Zone.huanan();
        } else if (HUABEI.equals(zone)) {
            return Zone.huabei();
        } else if (HUADONG.equals(zone)) {
            return Zone.huadong();
        } else if (BEIMEI.equals(zone)) {
            return Zone.beimei();
        } else if (XINJIAPO.equals(zone)) {
            return Zone.xinjiapo();
        }
        return Zone.autoZone();
    }
}
