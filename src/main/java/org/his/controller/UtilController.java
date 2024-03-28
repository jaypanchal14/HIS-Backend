package org.his.controller;


import lombok.extern.slf4j.Slf4j;
import org.his.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/his")
public class UtilController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail")
    public String sendEmail(){
        log.info("Request received for sending email");
        if(emailService.sendEmail("sample email", "brijesh.prajapati@iiitb.ac.in")){
            return "SENT";
        }else{
            return "FAILURE";
        }
    }

}