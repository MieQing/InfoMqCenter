package info.mq.infocore.config;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MailConfig {

    @Resource
    private JavaMailSender mailSender;


    public JavaMailSender getMailSender() {
        return mailSender;
    }
}
