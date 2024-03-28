package org.his.util;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendEmail(String msg, String emailTo){
        try {
            MimeMessage mimeMsg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMsg);
            helper.setFrom(sender);
            //helper.addTo("jay.panchal@iiitb.ac.in");
            helper.addTo(emailTo);
            helper.setSubject("Mail from HIS-Application");
            helper.setText(msg+"aur bete, so jao, bahot padh liya.");
            mailSender.send(mimeMsg);
            log.info("Mail sent");
        }catch (Exception e){
            log.error("Exception occurred with msg:"+e.getMessage());
            return false;
        }
        return true;
    }

}