package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PatientResponse;
import org.his.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class PatientController {

    @Autowired
    private PatientService patientService;


    //Will be called by Doctor and Nurse to get IP and OP patients
    @GetMapping("/patient/viewLivePatients")
    public ResponseEntity<PatientResponse> viewLivePatients(
            @RequestParam(name="role") String role,
            @RequestParam(name="isOP") int isOP,
            @RequestParam(name="userId") String userId
    ){
        log.info("viewLivePatients | Request received for getting active patients.");
        PatientResponse resp = patientService.viewLivePatients(role, userId, isOP);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/viewOneLivePatient")
    public ResponseEntity<PatientResponse> viewOneLivePatient(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "id") String id,
            @RequestParam(name = "patientId") String patientId
    ) {
        log.info("viewOneLivePatient | Request received for getting one live patients.");

        PatientResponse resp = patientService.viewOneLivePatient(role, id, patientId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

}