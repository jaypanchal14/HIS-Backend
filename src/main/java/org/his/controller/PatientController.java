package org.his.controller;

import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> getBothPatients(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "isOP") int isOP
            ){
        log.info("getBothPatients | Request received to get the IP/OP patients");
        //patientService.getPatients(role, userId, isOP);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

}