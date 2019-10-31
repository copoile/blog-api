package cn.poile.blog.common.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: yaohw
 * @create: 2019-10-30 18:57
 **/
@ConfigurationProperties(prefix = "oss")
@Data
public class StorageProperties {

    private int type;

    private final StorageProperties.Netease netease = new StorageProperties.Netease();

    private final StorageProperties.Qiniu qiniu = new StorageProperties.Qiniu();


    public StorageProperties() {

    }

    public static class Netease {
        private String accessKey;
        private String secretKey;
        private String endpoint;
        private String bucket;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }

    public static class Qiniu {
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String prefix;
        private String domain;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }
}
