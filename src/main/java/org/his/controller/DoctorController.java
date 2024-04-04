package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PatientResponse;
import org.his.service.DoctorService;
import org.his.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/his")
@CrossOrigin
@Slf4j
public class DoctorController {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/doc/viewPastPatients")
    public ResponseEntity<PatientResponse> viewLivePatients(
//            @RequestParam(name="role") String role,
//            @RequestParam(name="id") String id
    ){
        PatientResponse response = doctorService.viewPastPatients();
        log.info(response.toString());
        if (response.getResponse() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
