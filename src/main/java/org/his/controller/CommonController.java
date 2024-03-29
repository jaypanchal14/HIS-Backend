package org.his.controller;

import lombok.extern.slf4j.Slf4j;
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
        log.info("Request received for getting personal details.");
        PersonalDetailResp resp = service.getPersonalDetail(id, role);
        if (resp.getResponse() != null) {
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<PersonalDetailResp> updateProfile(
            @RequestParam("role") String role,
            @RequestParam("id") String id,
            @ModelAttribute PersonalDetail profileData) {
        PersonalDetailResp response = service.updateProfile(id, role, profileData);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/scheduleDetails")
    public ResponseEntity<?> getScheduleDetails(@RequestParam(name="email") String email, @RequestParam(name="role") String role){
        log.info("Request received for getting schedule details.");
        ScheduleDetailResp resp = service.getScheduleDetail(email, role);
        if (resp.getResponse() != null) {
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

}