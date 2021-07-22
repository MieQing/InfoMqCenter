package info.mq.admincenter.alarm.impl;


import info.mq.admincenter.alarm.DoAlarm;
import info.mq.infocore.config.MailConfig;
import info.mq.infocore.model.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class EmailAlarm implements DoAlarm {

    private static Logger logger = LoggerFactory.getLogger(EmailAlarm.class);


    @Autowired
    private MailConfig mailConfig;

    @Value("${spring.mail.username}")
    private String emailUserName;

    /*
    *发送邮件
     */
    @Override
    public boolean doAlarm(MessageInfo messageInfo) {
        Set<String> emailSet = new HashSet<String>(Arrays.asList(messageInfo.getNotifier().split(",")));
        boolean result=true;
        for (String email: emailSet) {
            try {
                System.out.println(">>>>>>>>>>> info-mq,  alarm email send start, message:{}");
                logger.info(">>>>>>>>>>> info-mq,  alarm email send start, message:{}", messageInfo.toString());
                MimeMessage mimeMessage = mailConfig.getMailSender().createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(emailUserName, "信息化队列平台");
                helper.setTo(email);
                helper.setSubject(messageInfo.getTheme());
                helper.setText(messageInfo.getMsessage(), true);
                mailConfig.getMailSender().send(mimeMessage);
                logger.info(">>>>>>>>>>> info-mq,  alarm email send end, message:{}", messageInfo.toString());
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> info-mq,  alarm email send error, message:{}", messageInfo.toString(), e);
                result = false;
            }


        }
        return result;
    }
}
