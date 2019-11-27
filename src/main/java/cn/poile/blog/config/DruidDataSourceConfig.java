package cn.poile.blog.config;

import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里数据库连接池配置、监控配置
 * @author: yaohw
 * @create: 2019-10-23 16:07
 **/
@Configuration
public class DruidDataSourceConfig {


    @Autowired
    private DruidStatProperties druidStatProperties;


    /**
     * 配置Druid监控的StatViewServlet和WebStatFilter
     */
    @Bean
    public ServletRegistrationBean druidServlet(){
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new StatViewServlet());
        //监控页面http://127.0.0.1:8081/druid/sql.html
        servletRegistrationBean.addUrlMappings("/druid/*");
        Map<String, String> initParameters = new HashMap<String, String>(3);
        initParameters.put("loginUsername", druidStatProperties.getStatViewServlet().getLoginUsername());
        initParameters.put("loginPassword", druidStatProperties.getStatViewServlet().getLoginPassword());
        initParameters.put("resetEnable", "true");
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
