package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.PatientResponse;
import org.his.service.DoctorService;
import org.his.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/his")
@CrossOrigin
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/doc/viewPastPatients")
    public ResponseEntity<PatientResponse> viewLivePatients(){
        PatientResponse resp = doctorService.viewPastPatients();
//        log.info(response.toString());
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
