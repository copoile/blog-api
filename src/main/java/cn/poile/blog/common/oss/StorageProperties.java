package cn.poile.blog.common.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: yaohw
 * @create: 2019-10-30 18:57
 **/
@Component
@ConfigurationProperties(prefix = "oss")
public class StorageProperties {

    private int type;

    private final StorageProperties.Nos nos = new StorageProperties.Nos();

    private final StorageProperties.Lettuce lettuce = new StorageProperties.Lettuce();

    public StorageProperties() {

    }

    public static class Lettuce {
        private String accessKey;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }
    }

    public static class Nos {
        private String accessKey;
        private String secretKey;
        private int maxConnections;
        private int socketTimeout;
        private int maxErrorRetry;
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

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getMaxErrorRetry() {
            return maxErrorRetry;
        }

        public void setMaxErrorRetry(int maxErrorRetry) {
            this.maxErrorRetry = maxErrorRetry;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Nos getNos() {
        return nos;
    }

    public Lettuce getLettuce() {
        return lettuce;
    }
}
