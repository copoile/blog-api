package cn.poile.blog.common.limiter.aspect;

import cn.poile.blog.common.limiter.annotation.RateLimiter;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.limiter.AdditionalLimiter;
import cn.poile.blog.common.limiter.Limit;
import cn.poile.blog.common.util.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
import java.util.List;
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
public class RateLimiterAspect implements ApplicationContextAware {

    private final static String SEPARATOR = ":";
    private final static String REDIS_LIMIT_KEY_PREFIX = "limit:";
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> limitRedisScript;
    private ApplicationContext applicationContext;

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(cn.poile.blog.common.limiter.annotation.RateLimiter)")
    public void rateLimit() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 通过 AnnotationUtils.findAnnotation 获取 RateLimiter 注解
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (rateLimiter != null && !rateLimiter.onlyAdditional()) {
            String key = rateLimiter.key();
            if (StringUtils.isBlank(key)) {
                key = rateLimiter.name();
            } else {
                key = rateLimiter.name() + SEPARATOR + generateKeyBySpEL(key, point);
            }
            if (rateLimiter.appendIp()) {
                key = key + SEPARATOR + IpUtil.getIpAddr();
            }
            long max = rateLimiter.max();
            long timeout = rateLimiter.timeout();
            TimeUnit timeUnit = rateLimiter.timeUnit();
            Limit limit = new Limit();
            limit.setKey(key);
            limit.setMax(max);
            limit.setTimeout(timeout);
            limit.setTimeUnit(timeUnit);
            limit.setMessage(rateLimiter.message());
            handleLimit(limit);
        }
        // 附加限流
        if (rateLimiter != null && !StringUtils.isBlank(rateLimiter.additional())) {
            AdditionalLimiter limiter =(AdditionalLimiter) applicationContext.getBean(rateLimiter.additional());
            if (limiter != null) {
                List<Limit> limit = limiter.limit(rateLimiter, point);
                limit.forEach(this::handleLimit);
            }
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
     * @return
     */
    private void handleLimit(Limit limit) {
        String key = REDIS_LIMIT_KEY_PREFIX + limit.getKey();
        long ttl = limit.getTimeUnit().toMillis(limit.getTimeout());
        long now = Instant.now().toEpochMilli();
        long max = limit.getMax();
        long expired = now - ttl;
        // 注意这里必须转为 String,否则会报错 java.lang.Long cannot be cast to java.lang.String
        Long executeTimes = stringRedisTemplate.execute(limitRedisScript, Collections.singletonList(key), now + "", ttl + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("【{}】在单位时间 {} 毫秒内已达到访问上限，当前接口上限 {}", key, ttl, max);
                throw new ApiException(ErrorEnum.REQUEST_LIMIT.getErrorCode(),limit.getMessage());
            } else {
                log.info("【{}】在单位时间 {} 毫秒内访问 {} 次", key, ttl, executeTimes);
            }
        }
    }

}
