package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.GeneralResp;
import org.his.bean.PersonalDetail;
import org.his.bean.PersonalDetailResp;
import org.his.bean.ScheduleDetailResp;
import org.his.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/his")
public class CommonController {

    @Autowired
    private CommonService service;

    @GetMapping("/personalDetails")
    public ResponseEntity<?> getPersonalDetails(@RequestParam(name="id") String id, @RequestParam(name="role") String role){
        log.info("getPersonalDetails | Request received for getting personal details.");
        PersonalDetailResp resp = service.getPersonalDetail(id, role);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<PersonalDetailResp> updateProfile(
            @RequestBody PersonalDetail profileData) {
        log.info("updateProfile | Request received for updating personal details.");
        PersonalDetailResp resp = service.updateProfile(profileData);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/scheduleDetails")
    public ResponseEntity<?> getScheduleDetails(@RequestParam(name="email") String email, @RequestParam(name="role") String role){
        log.info("getScheduleDetails | Request received for getting schedule details.");
        ScheduleDetailResp resp = service.getScheduleDetail(email, role);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/helpEmail")
    public ResponseEntity<?> sendHelpEmail(
            @RequestParam(name = "userId")String userId,
            @RequestParam(name="role") String role,
            @RequestBody GeneralResp request){
        log.info("sendHelpEmail | Request received for sending email to admin");
        GeneralResp resp = service.sendHelpEmail(userId, role, request);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

}