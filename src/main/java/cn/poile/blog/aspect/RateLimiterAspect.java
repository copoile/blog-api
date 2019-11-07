package cn.poile.blog.aspect;

import cn.poile.blog.annotation.RateLimiter;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 *
 * @author: yaohw
 * @create: 2019/11/5 10:49 下午
 */
@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Log4j2
public class RateLimiterAspect {

    private final static String SEPARATOR = ":";
    private final static String REDIS_LIMIT_KEY_PREFIX = "limit:";
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> limitRedisScript;

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(cn.poile.blog.annotation.RateLimiter)")
    public void rateLimit() {

    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 通过 AnnotationUtils.findAnnotation 获取 RateLimiter 注解
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (rateLimiter != null) {
            String key = rateLimiter.key();
            if (StringUtils.isBlank(key)) {
                key = rateLimiter.name();
            } else {
                key = rateLimiter.name() + SEPARATOR + generateKeyBySpEL(key, point);
            }
            long max = rateLimiter.max();
            long timeout = rateLimiter.timeout();
            TimeUnit timeUnit = rateLimiter.timeUnit();
            handleLimit(key, max, timeout, timeUnit);
        }
        return point.proceed();
    }

    /**
     * SpEL表达式缓存Key生成器.
     * 注解中传入key参数，则使用此生成器生成缓存.
     *
     * @param spELString
     * @param joinPoint
     * @return
     */
    private String generateKeyBySpEL(String spELString, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return expression.getValue(context).toString();
    }

    /**
     * 限流处理
     *
     * @param key
     * @param max
     * @param timeout
     * @param timeUnit
     * @return
     */
    private void handleLimit(String key, long max, long timeout, TimeUnit timeUnit) {
        key = REDIS_LIMIT_KEY_PREFIX + key;
        // 统一使用单位毫秒
        long ttl = timeUnit.toMillis(timeout);
        // 当前时间毫秒数
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;
        // 注意这里必须转为 String,否则会报错 java.lang.Long cannot be cast to java.lang.String
        Long executeTimes = stringRedisTemplate.execute(limitRedisScript, Collections.singletonList(key), now + "", ttl + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("【{}】在单位时间 {} 毫秒内已达到访问上限，当前接口上限 {}", key, ttl, max);
                throw new ApiException(ErrorEnum.REQUEST_LIMIT.getErrorCode(),"在单位时间" + parseTime(ttl) + "内已到达访问上限,当前接口上限 " + max);
            } else {
                log.info("【{}】在单位时间 {} 毫秒内访问 {} 次", key, ttl, executeTimes);
            }
        }
    }

    /**
     * 个性化时间转换
     *
     * @param millis 毫秒
     * @return
     */
    private String parseTime(long millis) {
        long second = 1000L;
        long minute = 60000L;
        long hour = 3600000L;
        long day = 86400000L;
        if (millis > day) {
            return TimeUnit.MILLISECONDS.toDays(millis) + "天";
        } else if (millis > hour) {
            return TimeUnit.MILLISECONDS.toHours(millis) + "小时";
        } else if (millis > minute) {
            return TimeUnit.MILLISECONDS.toMinutes(millis) + "分钟";
        } else if (millis > second) {
            return TimeUnit.MILLISECONDS.toSeconds(millis) + "秒";
        }
        return millis + "毫秒";
    }
}
