package cn.poile.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 博客应用入口
 * @author: yaohw
 * @create: 2019-10-23 10:44
 **/
@SpringBootApplication
@MapperScan("cn.poile.blog.mapper")
@EnableAsync
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class,args);
    }
}
