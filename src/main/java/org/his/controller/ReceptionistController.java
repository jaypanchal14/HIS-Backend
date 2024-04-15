package org.his.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.*;
import org.his.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class ReceptionistController {

    @Autowired
    private ReceptionistService receptionistService;


    /*
    @GetMapping("/reception/home")
    public ResponseEntity<?> getHome(){
        //As we are passing personalDetails with commonController endpoint, we are not going to implement this.
        // On the home screen of the receptionist, no other details is required
        return null;
    }*/

    @GetMapping("/reception/getAvailableDoctor")
    public ResponseEntity<ReceptionDetailResp> getAvailableDoctor(@RequestParam("userId") String userId) {
        log.info("getAvailableDoctor | request received to view available doctor");
        ReceptionDetailResp resp = receptionistService.getAvailableDoctor(userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/reception/registerPatient")
    public ResponseEntity<?> registerPatient(@ModelAttribute PatientDetail request) {
        log.info("registerPatient | request received to register new patient");
//        return ResponseEntity.status(HttpStatus.OK).body("OK");
        GeneralResp resp = receptionistService.registerPatient(request);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/reception/isPatientPresent")
    public ResponseEntity<PatientResponse> isPatientPresent(@RequestBody PatientDetail patientDetail) {
        log.info("isPatientPresent | request received for checking patient");
        PatientResponse resp = receptionistService.isPatientPresent(patientDetail);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
