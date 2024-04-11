package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PatientResponse;
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
        if (resp.getResponse() != null) {
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<PersonalDetailResp> updateProfile(
            @RequestBody PersonalDetail profileData) {
        log.info("updateProfile | Request received for updating personal details.");
        PersonalDetailResp response = service.updateProfile(profileData);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/scheduleDetails")
    public ResponseEntity<?> getScheduleDetails(@RequestParam(name="email") String email, @RequestParam(name="role") String role){
        log.info("getScheduleDetails | Request received for getting schedule details.");
        ScheduleDetailResp resp = service.getScheduleDetail(email, role);
        if (resp.getResponse() != null) {
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/viewLivePatients")
    public ResponseEntity<PatientResponse> viewLivePatients(
            @RequestParam(name="role") String role,
            @RequestParam(name="isOP") int isOP,
            @RequestParam(name="id") String id
    ){
        log.info("viewLivePatients | Request received for getting live patients.");
        PatientResponse response = service.viewLivePatients(role, id, isOP);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/viewOneLivePatient")
    public ResponseEntity<PatientResponse> viewOneLivePatient(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "id") String id,
            @RequestParam(name = "patientId") String patientId
    ) {
        log.info("viewOneLivePatient | Request received for getting one live patients.");

        PatientResponse response = service.viewOneLivePatient(role, id, patientId);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}