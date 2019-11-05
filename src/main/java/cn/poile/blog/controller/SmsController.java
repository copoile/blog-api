package cn.poile.blog.controller;

import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.sms.SmsService;
import cn.poile.blog.common.validator.IsPhone;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author: yaohw
 * @create: 2019-11-05 09:34
 **/
@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController{

    @Autowired
    private SmsService smsService;

    @GetMapping("/send_code")
    public ApiResponse sendSmsCode(@NotNull @IsPhone @RequestParam long mobile) {
        smsService.sendSmsCode(mobile);
        return createResponse();
    }


}
