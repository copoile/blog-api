package cn.poile.blog.common.email;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author: yaohw
 * @create: 2019-11-07 19:21
 **/
@Service
@Log4j2
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    public void test() {
        MimeMessage message;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //发送者
            helper.setFrom(new InternetAddress("15625295093@163.com", "个人悦读分享"));
            helper.setTo("726856005@qq.com");
            helper.setSubject("人悦读分享邮箱验证");
            Context context = new Context();
            StringBuilder sb=new StringBuilder("http://www.baidu.com");
            sb.append("?");
            sb.append("token=");
            sb.append("accessToken");
            context.setVariable("checkUrl", sb.toString());
            //设置html模板，这里为template文件夹下的emil.html模板
            String emailContent = templateEngine.process("email", context);
            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败:{}",e);
        }
    }

}
