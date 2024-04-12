package org.his.controller;

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

    @GetMapping("/reception/viewSchedule")
    public ResponseEntity<ReceptionDetailResp> viewSchedule(
            @RequestParam("role") String role,
            @RequestParam("id") String id
    ) {
        ReceptionDetailResp resp = receptionistService.viewSchedule(id, role);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/reception/registerPatient/{receptionistId}")
    public ResponseEntity<GeneralResp> registerPatient(
            @PathVariable String receptionistId,
            @ModelAttribute PatientDetail patientDetail
    ) {
        GeneralResp resp = receptionistService.registerPatient(receptionistId, patientDetail);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @PostMapping("/reception/isPatientPresent")
    public ResponseEntity<PatientResponse> isPatientPresent(@RequestBody PatientDetail patientDetail) {
        PatientResponse resp = receptionistService.isPatientPresent(patientDetail);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
