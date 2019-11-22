package cn.poile.blog.controller;

import cn.poile.blog.annotation.RateLimiter;
import cn.poile.blog.common.email.EmailService;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.validator.annotation.IsPhone;
import cn.poile.blog.websock.CustomWebSocketHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: yaohw
 * @create: 2019-11-05 09:34
 **/
@RestController
@RequestMapping("/sms")
@Log4j2
public class SmsController extends BaseController{

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomWebSocketHandler webSocketHandler;


    @PostMapping("/send")
    @RateLimiter(name = "sms",max = 1,key = "#mobile",timeUnit = TimeUnit.MINUTES,timeout = 2)
    public ApiResponse sendSmsCode(@NotNull @IsPhone @RequestParam long mobile) {
        smsCodeService.sendSmsCode(mobile);
        return createResponse();
    }

    @GetMapping("/test")
    public ApiResponse test() {
        Map<String,Object> params = new HashMap<>(1);
        params.put("checkUrl","http://www.baidu.com");
        emailService.sendHtmlMail("726856005@qq.com","邮箱验证","email",params,null);
        return createResponse();
    }

}
