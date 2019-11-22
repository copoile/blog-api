package cn.poile.blog.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流注解
 * @author: yaohw
 * @create: 2019/11/5 10:28 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    long DEFAULT_REQUEST = 10;

    /**
     * 名称
     */
    String name();

    /**
     * max 最大请求数
     */
    @AliasFor("max") long value() default DEFAULT_REQUEST;

    /**
     * max 最大请求数
     */
    @AliasFor("value") long max() default DEFAULT_REQUEST;

    /**
     * 限流key，支持SpEL
     */
    String key() default "";

    /**
     * 在多久时常内
     */
    long timeout() default 60;

    /**
     * 超时时间单位 默认为 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 自定义附加限流器
     * @return
     */
    String additional() default "";
}
