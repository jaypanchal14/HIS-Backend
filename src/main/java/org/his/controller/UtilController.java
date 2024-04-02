package org.his.controller;


import lombok.extern.slf4j.Slf4j;
import org.his.util.EmailService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class UtilController {

    @Autowired
    private EmailService emailService;

    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    @GetMapping("/sendEmail")
    public String sendEmail(){
        log.info("Request received for sending email");
        if(emailService.sendEmail("sample email", "brijesh.prajapati@iiitb.ac.in")){
            return "SENT";
        }else{
            return "FAILURE";
        }
    }

    @GetMapping("/getCipher")
    public String tryEncryption(@RequestParam String param){
        String cipher = stringEncryptor.encrypt(param);
        System.out.println("Encrypted : "+cipher);
        String plain = stringEncryptor.decrypt(cipher);
        System.out.println("Decrypted : "+plain);
        return cipher;
    }

}