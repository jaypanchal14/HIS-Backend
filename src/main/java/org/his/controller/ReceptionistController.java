package org.his.controller;

import org.his.bean.*;
import org.his.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/his")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    @Autowired
    public ReceptionistController(ReceptionistService receptionistService) {
        this.receptionistService = receptionistService;
    }

    @GetMapping("/reception/viewSchedule")
    public ResponseEntity<ReceptionDetailResp> viewSchedule(
            @RequestParam("role") String role,
            @RequestParam("id") String id
    ) {
        ReceptionDetailResp response = receptionistService.viewSchedule(id, role);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/reception/registerPatient/{receptionistId}")
    public ResponseEntity<GeneralResp> registerPatient(
            @PathVariable String receptionistId,
            @ModelAttribute PatientDetail patientDetail
    ) {
        GeneralResp response = receptionistService.registerPatient(receptionistId, patientDetail);
        if (response.getResponse().equals("SUCCESS")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reception/isPatientPresent")
    public ResponseEntity<PatientResponse> isPatientPresent(@RequestBody PatientDetail patientDetail) {
        PatientResponse response = receptionistService.isPatientPresent(patientDetail);
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
