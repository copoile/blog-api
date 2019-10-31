package cn.poile.blog.common.oss;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yaohw
 * @create: 2019-10-31 10:50
 **/
@Configuration
@EnableConfigurationProperties({StorageProperties.class})
@Log4j2
public class StorageConfiguration  {

    @Bean
    @ConditionalOnMissingBean(name = {"storage"})
    public Storage storage(StorageProperties properties) {
        return StorageFactory.build(properties);
    }
}
