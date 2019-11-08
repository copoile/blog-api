package cn.poile.blog.common.security;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略security校验配置
 * @author: yaohw
 * @create: 2019-11-08 15:04
 **/
@Data
@Configuration
@ConditionalOnExpression("!'${ignore}'.isEmpty()")
@ConfigurationProperties(prefix = "ignore")
public class SecurityIgnoreProperties {

    private List<String> list = new ArrayList<>();
}
