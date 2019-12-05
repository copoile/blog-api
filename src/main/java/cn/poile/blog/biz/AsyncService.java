package cn.poile.blog.biz;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * 通用异步服务
 * @author: yaohw
 * @create: 2019-12-03 15:39
 **/
@Log4j2
@Component
public class AsyncService {


    @Async
    public void runAsync(Function<Boolean, Boolean> function) {
        try {
            function.apply(Boolean.TRUE);
        } catch (Exception e) {
            log.error("异步任务执行错误:{0}",e);
        }

    }

}
