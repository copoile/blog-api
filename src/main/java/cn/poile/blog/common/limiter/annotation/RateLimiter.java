package cn.poile.blog.common.limiter.annotation;

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
     * 只启动附加
     * @return
     */
    boolean onlyAdditional() default false;

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
     * 是否拼接ip
     * @return
     */
    boolean appendIp() default false;

    /**
     * 自定义附加限流器bean名称
     * @return
     */
    String additional() default "";

    /**
     * 限流信息
     * @return
     */
    String message() default "接口调用频繁，请稍后再试";
}
