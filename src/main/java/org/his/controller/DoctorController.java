package org.his.controller;

import lombok.extern.slf4j.Slf4j;
import org.his.bean.DashboardResponse;
import org.his.bean.GeneralResp;
import org.his.bean.PatientResponse;
import org.his.service.DoctorService;
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

    //Not being used
    @GetMapping("/doc/viewPastPatients")
    public ResponseEntity<PatientResponse> viewPastPatients(@RequestParam(name = "userId") String userId){
        log.info("viewPastPatients | request received to view past-patients");
        PatientResponse resp = doctorService.viewPastPatients(userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/doc/home")
    public ResponseEntity<?> dashboard(@RequestParam(name = "userId") String userId){
        log.info("dashboard | request received to get dashboard-data");
        DashboardResponse resp = doctorService.getDashBoard(userId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }

    @GetMapping("/doc/handleEmergency")
    public ResponseEntity<?> handleEmergency(@RequestParam(name = "userId") String userId,
                                             @RequestParam(name = "emerId") String emerId ){
        log.info("dashboard | request received to handleEmergency by doctor:"+userId+", emergencyId:"+emerId);
        GeneralResp resp = doctorService.handleEmergency(userId, emerId);
        if(resp.getError() == null){
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }


}
