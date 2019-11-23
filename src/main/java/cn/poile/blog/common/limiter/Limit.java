package cn.poile.blog.common.limiter;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author: yaohw
 * @create: 2019-11-23 10:20
 **/
@Data
public class Limit {

    /**
     * redis 限流key
     */
    private String key;

    /**
     * 限流次数
     */
    private long max;

    /**
     * 限流时间
     */
    private long timeout;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 限流信息
     */
    private String message;
}
