package org.his.util;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.GeneralResp;
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

    public boolean sendEmail(String msg, String emailTo, boolean isHTML){
        try {
            MimeMessage mimeMsg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMsg);
            helper.setFrom(sender);
            //helper.addTo("jay.panchal@iiitb.ac.in");
            helper.addTo(emailTo);
            helper.setSubject("Mail from HIS-Application");
            helper.setText(msg, isHTML);

            mailSender.send(mimeMsg);
//            log.info("Mail sent");
        }catch (Exception e){
            log.error("Exception occurred with msg:"+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean sendEmailWithPassword(String email, String newPass){
        try{
            if(email == null || email.isEmpty()){
                throw new Exception("Empty email address passed.");
            }
            MimeMessage mimeMsg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMsg);
            helper.setFrom(sender);
            //helper.addTo("jay.panchal@iiitb.ac.in");
            helper.addTo(email);
            helper.setSubject("Password reset email from HIS-Application");
            helper.setText(Utility.initialForgotPass+newPass+Utility.endForgotPass);
            mailSender.send(mimeMsg);
//            log.info("Email sent to the user with password");
        }catch (Exception e){
            log.error("Exception occurred with msg: "+e.getMessage());
            return false;
        }
        return true;
    }

}