package cn.poile.blog.common.limiter;

import cn.poile.blog.common.limiter.annotation.RateLimiter;
import cn.poile.blog.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 短信接口附加限流,ip限流，同一手机号一天内限流
 *
 * @author: yaohw
 * @create: 2019-11-22 17:35
 **/
public class SmsLimiter implements ExtraLimiter {


    private final static String SEPARATOR = ":";
    private final static String DAY = "day:";
    private final static TimeUnit TIMEUNIT = TimeUnit.SECONDS;

    /**
     * 限流次数
     */
    private long max = 10L;

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    /**
     * 短信附加限流，同一手机号每天只能调用10次
     *
     * @param rateLimiter 限流注解
     * @param point aop 切面point
     * @return key 为限流key，value为限流对象的map
     */
    @Override
    public List<Limit> limit(RateLimiter rateLimiter, ProceedingJoinPoint point) {
        List<Limit> list = new ArrayList<>();
        Limit ipLimit = ipLimit(rateLimiter);
        if (ipLimit != null) {
            list.add(ipLimit);
        }
        Limit dayLimit = dayLimit(rateLimiter, point);
        if (dayLimit != null) {
            list.add(dayLimit);
        }
        return list;
    }

    /**
     * ip 限流
     * @param rateLimiter
     * @return
     */
    private Limit ipLimit(RateLimiter rateLimiter) {
        String key = rateLimiter.name() + SEPARATOR + IpUtil.getIpAddr();
        Limit limit = new Limit();
        limit.setKey(key);
        limit.setMax(rateLimiter.max());
        limit.setTimeout(rateLimiter.timeout());
        limit.setTimeUnit(rateLimiter.timeUnit());
        limit.setMessage("接口调用频繁，请稍后再试");
        return limit;
    }

    /**
     * 当天同一手机号次数限制
     * @param rateLimiter
     * @param point
     * @return
     */
    private Limit dayLimit(RateLimiter rateLimiter, ProceedingJoinPoint point) {
        String key = rateLimiter.key();
        if (StringUtils.isBlank(key)) {
            return null;
        } else {
            // sms:day:15625295093
            key = rateLimiter.name() + SEPARATOR + DAY + generateKeyBySpEL(key, point);
        }
        Limit limit = new Limit();
        limit.setKey(key);
        limit.setMax(max);
        limit.setTimeout(getRemainSecondsOneCurrentDay());
        limit.setTimeUnit(TIMEUNIT);
        limit.setMessage("超出接口调上限，一天只能调用" + max + "次");
       return limit;
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
        DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return expression.getValue(context).toString();
    }

    /**
     * 获取当天时间距离当天结束时间戳
     * @return
     */
    private long getRemainSecondsOneCurrentDay() {
        Date currentDate = new Date();
        // 明天00:00
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        // 当前时间
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        return ChronoUnit.SECONDS.between(currentDateTime, midnight);
    }
}
