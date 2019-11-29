package cn.poile.blog.common.limiter;

import cn.poile.blog.common.limiter.annotation.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;

/**
 * 附加接口限流接口
 * @author: yaohw
 * @create: 2019-11-22 17:30
 **/
public interface ExtraLimiter {

    /**
     *  附加自定义限流
     * @param rateLimiter 限流注解
     * @param point aop 切面point
     * @return 限流对象列表
     */
    List<Limit> limit(RateLimiter rateLimiter, ProceedingJoinPoint point);
}
