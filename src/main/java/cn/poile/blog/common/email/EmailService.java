package cn.poile.blog.common.email;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * 邮件服务
 * @author: yaohw
 * @create: 2019-11-07 19:21
 **/
@Service
@Log4j2
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.jndi-name}")
    private String personal;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 异步发送Html邮件
     * @param to 发送给谁
     * @param subject 主题
     * @param template html模板名
     * @param params 模板参数
     * @param cc 抄送到
     */
    @Async
    public void asyncSendHtmlMail(String to, String subject, String template, Map<String, Object> params, String... cc) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from, personal);
            helper.setTo(to);
            helper.setSubject(subject);
            Context context = new Context();
            context.setVariables(params);
            String emailContent = templateEngine.process(template, context);
            helper.setText(emailContent, true);
            if (ArrayUtils.isNotEmpty(cc)) {
                helper.setCc(cc);
            }
            mailSender.send(message);
            log.info("邮件成功发送到:{}",to);
        } catch (Exception e) {
            log.error("发送邮件失败:{0}", e);
        }
    }

    /**
     * 发送Html邮件
     * @param to 发送给谁
     * @param subject 主题
     * @param template html模板名
     * @param params 模板参数
     * @param cc 抄送到
     */
    public void sendHtmlMail(String to, String subject, String template, Map<String, Object> params, String... cc) {
        asyncSendHtmlMail(to, subject, template, params, cc);
    }

    /**
     * 异步发送Html邮件
     * @param to 发送给谁
     * @param subject 主题
     * @param template html模板名
     * @param params 模板参数
     */
    @Async
    public void asyncSendHtmlMail(String to, String subject, String template, Map<String, Object> params) {
        String[] cc = new String[0];
        asyncSendHtmlMail(to, subject, template, params,cc);
    }

    /**
     * 发送Html邮件
     * @param to 发送给谁
     * @param subject 主题
     * @param template html 模板
     * @param params 模板参数
     */
    public void sendHtmlMail(String to, String subject, String template, Map<String, Object> params) {
       asyncSendHtmlMail(to, subject, template, params);
    }

}
