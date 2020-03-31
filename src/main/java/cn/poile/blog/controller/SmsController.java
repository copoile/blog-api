package cn.poile.blog.controller;

import cn.poile.blog.biz.EmailService;
import cn.poile.blog.common.constant.RedisConstant;
import cn.poile.blog.common.limiter.annotation.RateLimiter;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.validator.annotation.IsPhone;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author: yaohw
 * @create: 2019-11-05 09:34
 **/
@Log4j2
@RestController
@RequestMapping("/sms")
@Api(tags = "短信验证码服务",value = "/sms")
public class SmsController extends BaseController{

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    @RateLimiter(name = RedisConstant.SMS_LIMIT_NAME,max = 1,key = "#mobile", timeout = 120L, extra = "smsLimiter")
    @ApiOperation(value = "发送短信验证码",notes = "验证码有效时5分钟;同一手机号每天只能发10次;同一ip每天只能发10次;同一手机号限流120s一次")
    public ApiResponse sendSmsCode(@ApiParam("手机号") @NotNull @IsPhone @RequestParam long mobile) {
        smsCodeService.sendSmsCode(mobile);
        return createResponse();
    }

}
