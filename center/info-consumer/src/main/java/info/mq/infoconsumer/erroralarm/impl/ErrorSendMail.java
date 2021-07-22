package info.mq.infoconsumer.erroralarm.impl;

import info.mq.infoconsumer.consumer.InvokeProxy;
import info.mq.infoconsumer.erroralarm.DoErorAlarm;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import info.mq.infocore.config.MailConfig;
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

/*
* 报错发送邮件
* */
@Component
public class ErrorSendMail implements DoErorAlarm {
    private static Logger logger = LoggerFactory.getLogger(ErrorSendMail.class);

    @Autowired
    private MailConfig mailConfig;

    @Value("${spring.mail.username}")
    private String emailUserName;

    @Override
    public boolean doErrorAlarm(ConsumerResult consumerResult, Consumer_Task task) {
        Set<String> emailSet = new HashSet<String>(Arrays.asList(task.getNotifier().split(",")));
        boolean result=true;
        String mailContext=merageMes(consumerResult,task);
        for (String email: emailSet) {
            try {
                logger.info(">>>>>>>>>>> info-mq,  alarm email send start, message:{}", mailContext);
                MimeMessage mimeMessage = mailConfig.getMailSender().createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(emailUserName, "信息化队列平台");
                helper.setTo(email);
                helper.setSubject("消费消息错误");
                helper.setText(mailContext, true);
                mailConfig.getMailSender().send(mimeMessage);
                logger.info(">>>>>>>>>>> info-mq,  alarm email send end, message:{}", mailContext);
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> info-mq,  alarm email send error, message:{}", mailContext, e);
                result = false;
            }


        }
        return true;
    }

    private String merageMes(ConsumerResult consumerResult, Consumer_Task task){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("ErrorMsg List:");
        stringBuilder.append(consumerResult.toString());
        stringBuilder.append("Consumer_Task Config:");
        stringBuilder.append(task.toString());
        return stringBuilder.toString();
    }
}
